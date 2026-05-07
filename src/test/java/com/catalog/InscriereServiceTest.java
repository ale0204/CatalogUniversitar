package com.catalog;

import com.catalog.exception.BusinessRuleException;
import com.catalog.model.Curs;
import com.catalog.model.Student;
import com.catalog.repository.IUnitOfWork;
import com.catalog.repository.InMemoryUnitOfWork;
import com.catalog.service.CursService;
import com.catalog.service.InscriereService;
import com.catalog.service.StudentService;
import com.catalog.service.interfaces.ICursService;
import com.catalog.service.interfaces.IInscriereService;
import com.catalog.service.interfaces.IStudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InscriereServiceTest {
    private IUnitOfWork uow;
    private IInscriereService inscriereService;
    private ICursService cursService;
    private IStudentService studentService;

    @BeforeEach
    void setUp() {
        uow = new InMemoryUnitOfWork();
        inscriereService = new InscriereService(uow);
        cursService = new CursService(uow);
        studentService = new StudentService(uow);
    }

    @Test
    void inscrie_cursPlin_aruncarBusinessRuleException() {
        // Curs cu capacitate 1
        Curs curs = new Curs(1, 1, "2025-2026", 2, 1);
        cursService.insert(curs);

        Student s1 = new Student("Student1", 20);
        Student s2 = new Student("Student2", 21);
        studentService.insert(s1);
        studentService.insert(s2);

        inscriereService.inscrie(s1.getId(), curs.getId());

        // Al doilea student trebuie sa primeasca exceptie
        assertThrows(BusinessRuleException.class,
                () -> inscriereService.inscrie(s2.getId(), curs.getId()));
    }

    @Test
    void inscrie_studentDuplicat_aruncarBusinessRuleException() {
        Curs curs = new Curs(1, 1, "2025-2026", 2, 5);
        cursService.insert(curs);
        Student s = new Student("Student", 20);
        studentService.insert(s);

        inscriereService.inscrie(s.getId(), curs.getId());

        assertThrows(BusinessRuleException.class,
                () -> inscriereService.inscrie(s.getId(), curs.getId()));
    }
}
