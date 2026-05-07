package com.catalog.service;

import com.catalog.model.Materie;
import com.catalog.repository.IUnitOfWork;
import com.catalog.service.interfaces.IMaterieService;

public class MaterieService extends BaseService<Materie> implements IMaterieService {
    public MaterieService(IUnitOfWork unitOfWork) {
        super(unitOfWork.getMaterieRepository());
    }
}
