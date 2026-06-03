package com.catalog.repository;

import com.catalog.model.Curs;
import com.catalog.model.Inscriere;
import com.catalog.model.Materie;
import com.catalog.model.Nota;
import com.catalog.model.Profesor;
import com.catalog.model.Student;

public class InMemoryUnitOfWork implements IUnitOfWork {
    private final IRepository<Student> studentRepository;
    private final IRepository<Profesor> profesorRepository;
    private final IRepository<Materie> materieRepository;
    private final IRepository<Nota> notaRepository;
    private final IRepository<Curs> cursRepository;
    private final IRepository<Inscriere> inscriereRepository;

    public InMemoryUnitOfWork() {
        this.studentRepository   = new InMemoryRepository<>();
        this.profesorRepository  = new InMemoryRepository<>();
        this.materieRepository   = new InMemoryRepository<>();
        this.notaRepository      = new InMemoryRepository<>();
        this.cursRepository      = new InMemoryRepository<>();
        this.inscriereRepository = new InMemoryRepository<>();
    }

    @Override public IRepository<Student>   getStudentRepository()   { return studentRepository; }
    @Override public IRepository<Profesor>  getProfesorRepository()  { return profesorRepository; }
    @Override public IRepository<Materie>   getMaterieRepository()   { return materieRepository; }
    @Override public IRepository<Nota>      getNotaRepository()      { return notaRepository; }
    @Override public IRepository<Curs>      getCursRepository()      { return cursRepository; }
    @Override public IRepository<Inscriere> getInscriereRepository() { return inscriereRepository; }

    @Override public void commit()   { /* no-op in-memory */ }
    @Override public void rollback() { /* no-op in-memory */ }
}
