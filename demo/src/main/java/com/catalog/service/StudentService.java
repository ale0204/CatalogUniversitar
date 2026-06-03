package com.catalog.service;

import java.util.Comparator;
import java.util.TreeSet;

import com.catalog.model.Nota;
import com.catalog.model.Student;
import com.catalog.repository.IRepository;
import com.catalog.repository.IUnitOfWork;
import com.catalog.service.interfaces.IStudentService;
import com.catalog.util.AuditService;

public class StudentService extends BaseService<Student> implements IStudentService {
    private final IRepository<Nota> notaRepo;

    public StudentService(IUnitOfWork unitOfWork) {
        super(unitOfWork.getStudentRepository());
        this.notaRepo = unitOfWork.getNotaRepository();
    }

    @Override
    public void insert(Student student) {
        AuditService.getInstance().log("adaugaStudent");
        super.insert(student);
    }

    @Override
    public TreeSet<Student> getStudentiOrdonatiDupaMedie() {
        AuditService.getInstance().log("afiseazaStudentiOrdonatiDupaMedie");
        Comparator<Student> dupaMedieDesc = Comparator
                .comparingDouble((Student s) -> calculeazaMedie(s.getId()))
                .reversed()
                .thenComparing(s -> s.getNume());

        TreeSet<Student> sorted = new TreeSet<>(dupaMedieDesc);
        sorted.addAll(repository.getAll());
        return sorted;
    }

    private float calculeazaMedie(int studentId) {
        return (float) notaRepo.getAll().stream()
                .filter(n -> n.getStudentId() == studentId)
                .mapToDouble(Nota::getValoare)
                .average()
                .orElse(0.0);
    }
}
