package com.catalog.service;

import java.util.ArrayList;
import java.util.List;

import com.catalog.model.Curs;
import com.catalog.model.Materie;
import com.catalog.model.Profesor;
import com.catalog.repository.IRepository;
import com.catalog.repository.IUnitOfWork;
import com.catalog.service.interfaces.IProfesorService;

public class ProfesorService extends BaseService<Profesor> implements IProfesorService {
    private final IRepository<Curs> cursRepo;
    private final IRepository<Materie> materieRepo;

    public ProfesorService(IUnitOfWork unitOfWork) {
        super(unitOfWork.getProfesorRepository());
        this.cursRepo    = unitOfWork.getCursRepository();
        this.materieRepo = unitOfWork.getMaterieRepository();
    }

    @Override
    public List<Materie> getMateriiPredate(int profesorId) {
        List<Materie> result = new ArrayList<>();
        for (Curs curs : cursRepo.getAll()) {
            if (curs.getProfesorId() == profesorId) {
                Materie m = materieRepo.getById(curs.getMaterieId());
                if (m != null && !result.contains(m)) {
                    result.add(m);
                }
            }
        }
        return result;
    }
}
