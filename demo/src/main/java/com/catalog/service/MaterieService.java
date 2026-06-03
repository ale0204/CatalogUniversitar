package com.catalog.service;

import com.catalog.model.Materie;
import com.catalog.repository.IUnitOfWork;
import com.catalog.service.interfaces.IMaterieService;
import com.catalog.util.AuditService;

public class MaterieService extends BaseService<Materie> implements IMaterieService {
    public MaterieService(IUnitOfWork unitOfWork) {
        super(unitOfWork.getMaterieRepository());
    }

    @Override
    public void insert(Materie materie) {
        AuditService.getInstance().log("adaugaMaterie");
        super.insert(materie);
    }
}
