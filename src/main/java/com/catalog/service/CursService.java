package com.catalog.service;

import java.util.ArrayList;
import java.util.List;

import com.catalog.model.Curs;
import com.catalog.model.Inscriere;
import com.catalog.model.Student;
import com.catalog.repository.IRepository;
import com.catalog.repository.IUnitOfWork;
import com.catalog.service.interfaces.ICursService;

public class CursService extends BaseService<Curs> implements ICursService {
    private final IRepository<Inscriere> inscriereRepo;
    private final IRepository<Student> studentRepo;

    public CursService(IUnitOfWork unitOfWork) {
        super(unitOfWork.getCursRepository());
        this.inscriereRepo = unitOfWork.getInscriereRepository();
        this.studentRepo   = unitOfWork.getStudentRepository();
    }

    @Override
    public List<Student> getStudentiInscrisi(int cursId) {
        List<Student> result = new ArrayList<>();
        for (Inscriere i : inscriereRepo.getAll()) {
            if (i.getCursId() == cursId) {
                Student s = studentRepo.getById(i.getStudentId());
                if (s != null) result.add(s);
            }
        }
        return result;
    }
}
