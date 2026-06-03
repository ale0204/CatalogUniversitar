package com.catalog.service;

import java.util.List;
import java.util.stream.Collectors;

import com.catalog.model.Nota;
import com.catalog.repository.IUnitOfWork;
import com.catalog.service.interfaces.INotaService;
import com.catalog.util.AuditService;

public class NotaService extends BaseService<Nota> implements INotaService {
    public NotaService(IUnitOfWork unitOfWork) {
        super(unitOfWork.getNotaRepository());
    }

    @Override
    public void insert(Nota nota) {
        AuditService.getInstance().log("adaugaNota");
        super.insert(nota);
    }

    @Override
    public float calculeazaMedie(int studentId) {
        AuditService.getInstance().log("calculeazaMedieStudent");
        return (float) repository.getAll().stream()
                .filter(n -> n.getStudentId() == studentId)
                .mapToDouble(Nota::getValoare)
                .average()
                .orElse(0.0);
    }

    @Override
    public List<Nota> getNoteStudent(int studentId) {
        AuditService.getInstance().log("afiseazaNoteleStudentului");
        return repository.getAll().stream()
                .filter(n -> n.getStudentId() == studentId)
                .collect(Collectors.toList());
    }
}
