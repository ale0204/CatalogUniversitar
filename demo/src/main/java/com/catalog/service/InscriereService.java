package com.catalog.service;

import com.catalog.exception.BusinessRuleException;
import com.catalog.exception.EntityNotFoundException;
import com.catalog.model.Curs;
import com.catalog.model.Inscriere;
import com.catalog.model.enums.StatusInscriere;
import com.catalog.repository.IRepository;
import com.catalog.repository.IUnitOfWork;
import com.catalog.service.interfaces.IInscriereService;

public class InscriereService extends BaseService<Inscriere> implements IInscriereService {
    private final IRepository<Curs> cursRepo;

    public InscriereService(IUnitOfWork unitOfWork) {
        super(unitOfWork.getInscriereRepository());
        this.cursRepo = unitOfWork.getCursRepository();
    }

    @Override
    public Inscriere inscrie(int studentId, int cursId) {
        Curs curs = cursRepo.getById(cursId);
        if (curs == null) {
            throw new EntityNotFoundException("Curs", cursId);
        }

        long inscrisiActivi = repository.getAll().stream()
                .filter(i -> i.getCursId() == cursId && i.getStatus() == StatusInscriere.ACTIV)
                .count();

        if (inscrisiActivi >= curs.getMaxStudenti()) {
            throw new BusinessRuleException(
                String.format("Cursul %d este plin (%d/%d locuri ocupate).",
                        cursId, inscrisiActivi, curs.getMaxStudenti())
            );
        }

        boolean esteInscris = repository.getAll().stream()
                .anyMatch(i -> i.getStudentId() == studentId
                        && i.getCursId() == cursId
                        && i.getStatus() == StatusInscriere.ACTIV);

        if (esteInscris) {
            throw new BusinessRuleException(
                String.format("Studentul %d este deja inscris la cursul %d.", studentId, cursId)
            );
        }

        Inscriere inscriere = new Inscriere(studentId, cursId);
        repository.insert(inscriere);
        return inscriere;
    }

    @Override
    public void retrage(int studentId, int cursId) {
        Inscriere inscriere = repository.getAll().stream()
                .filter(i -> i.getStudentId() == studentId
                        && i.getCursId() == cursId
                        && i.getStatus() == StatusInscriere.ACTIV)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                    String.format("Inscriere activa pentru studentId=%d, cursId=%d", studentId, cursId), -1
                ));

        inscriere.setStatus(StatusInscriere.RETRAS);
        repository.update(inscriere);
    }
}
