package com.catalog.repository;

import com.catalog.model.Curs;
import com.catalog.model.Inscriere;
import com.catalog.model.Materie;
import com.catalog.model.Nota;
import com.catalog.model.Profesor;
import com.catalog.model.Student;

public interface IUnitOfWork {
    IRepository<Student> getStudentRepository();
    IRepository<Profesor> getProfesorRepository();
    IRepository<Materie> getMaterieRepository();
    IRepository<Nota> getNotaRepository();
    IRepository<Curs> getCursRepository();
    IRepository<Inscriere> getInscriereRepository();

    void commit();

    void rollback();
}
