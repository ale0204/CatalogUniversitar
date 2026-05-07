package com.catalog.service;

import java.util.List;
import java.util.stream.Collectors;

import com.catalog.model.Nota;
import com.catalog.repository.IUnitOfWork;
import com.catalog.service.interfaces.INotaService;

public class NotaService extends BaseService<Nota> implements INotaService {
    public NotaService(IUnitOfWork unitOfWork) {
        super(unitOfWork.getNotaRepository());
    }

    @Override
    public float calculeazaMedie(int studentId) {
        return (float) repository.getAll().stream()
                .filter(n -> n.getStudentId() == studentId)
                .mapToDouble(Nota::getValoare)
                .average()
                .orElse(0.0);
    }

    @Override
    public List<Nota> getNoteStudent(int studentId) {
        return repository.getAll().stream()
                .filter(n -> n.getStudentId() == studentId)
                .collect(Collectors.toList());
    }
}
