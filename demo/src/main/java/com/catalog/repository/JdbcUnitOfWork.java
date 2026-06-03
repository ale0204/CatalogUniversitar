package com.catalog.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.catalog.exception.CatalogException;
import com.catalog.model.Curs;
import com.catalog.model.Inscriere;
import com.catalog.model.Materie;
import com.catalog.model.Nota;
import com.catalog.model.Profesor;
import com.catalog.model.Student;
import com.catalog.repository.mappers.CursMapper;
import com.catalog.repository.mappers.InscriereMapper;
import com.catalog.repository.mappers.MaterieMapper;
import com.catalog.repository.mappers.NotaMapper;
import com.catalog.repository.mappers.ProfesorMapper;
import com.catalog.repository.mappers.StudentMapper;
import com.catalog.util.DatabaseConnection;
import com.catalog.util.IdGenerator;

// Implementarea JDBC a IUnitOfWork. Acelasi contract ca InMemoryUnitOfWork,
// dar repo-urile vorbesc cu PostgreSQL. Serviciile nu observa diferenta.
public class JdbcUnitOfWork implements IUnitOfWork {
    private final Connection connection;

    private final IRepository<Student> studentRepository;
    private final IRepository<Profesor> profesorRepository;
    private final IRepository<Materie> materieRepository;
    private final IRepository<Nota> notaRepository;
    private final IRepository<Curs> cursRepository;
    private final IRepository<Inscriere> inscriereRepository;

    public JdbcUnitOfWork() {
        this.connection = DatabaseConnection.getInstance().getConnection();

        this.studentRepository   = new JdbcRepository<>(connection, new StudentMapper());
        this.profesorRepository  = new JdbcRepository<>(connection, new ProfesorMapper());
        this.materieRepository   = new JdbcRepository<>(connection, new MaterieMapper());
        this.notaRepository      = new JdbcRepository<>(connection, new NotaMapper());
        this.cursRepository      = new JdbcRepository<>(connection, new CursMapper());
        this.inscriereRepository = new JdbcRepository<>(connection, new InscriereMapper());

        seedIdGenerator();
    }

    // Citeste MAX(id) din fiecare tabela si seteaza contorul IdGenerator la MAX+1.
    // Astfel entitatile noi create in aceasta rulare nu se ciocnesc cu randurile deja existente.
    private void seedIdGenerator() {
        seedFor(Student.class, "student");
        seedFor(Profesor.class, "profesor");
        seedFor(Materie.class, "materie");
        seedFor(Nota.class, "nota");
        seedFor(Curs.class, "curs");
        seedFor(Inscriere.class, "inscriere");
    }

    private void seedFor(Class<?> entityClass, String tableName) {
        String sql = "SELECT COALESCE(MAX(id), 0) FROM " + tableName;
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            int maxId = rs.getInt(1);
            IdGenerator.seed(entityClass, maxId + 1);
        } catch (SQLException e) {
            throw new CatalogException("Eroare la seed IdGenerator pentru " + tableName + ": " + e.getMessage());
        }
    }

    // Goleste toate tabelele si reseteaza contoarele de id.
    // RESTART IDENTITY => secventele repornesc; CASCADE => respecta foreign keys (sterge in ordinea corecta).
    // Dupa golire, re-seedam IdGenerator ca noile entitati sa porneasca iar de la id=1.
    public void curataToateTabelele() {
        String sql = "TRUNCATE inscriere, nota, curs, materie, profesor, student RESTART IDENTITY CASCADE";
        try (Statement st = connection.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new CatalogException("Eroare la curatarea tabelelor: " + e.getMessage());
        }
        seedIdGenerator();
    }

    @Override public IRepository<Student>   getStudentRepository()   { return studentRepository; }
    @Override public IRepository<Profesor>  getProfesorRepository()  { return profesorRepository; }
    @Override public IRepository<Materie>   getMaterieRepository()   { return materieRepository; }
    @Override public IRepository<Nota>      getNotaRepository()      { return notaRepository; }
    @Override public IRepository<Curs>      getCursRepository()      { return cursRepository; }
    @Override public IRepository<Inscriere> getInscriereRepository() { return inscriereRepository; }

    // autoCommit=true (vezi DatabaseConnection) => fiecare operatie e deja salvata.
    // Pastram commit/rollback cablate la conexiunea reala pentru cazul cand ar fi nevoie de tranzactii.
    @Override
    public void commit() {
        try {
            if (!connection.getAutoCommit()) connection.commit();
        } catch (SQLException e) {
            throw new CatalogException("Eroare la commit: " + e.getMessage());
        }
    }

    @Override
    public void rollback() {
        try {
            if (!connection.getAutoCommit()) connection.rollback();
        } catch (SQLException e) {
            throw new CatalogException("Eroare la rollback: " + e.getMessage());
        }
    }
}
