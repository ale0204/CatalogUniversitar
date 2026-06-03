package com.catalog;

import com.catalog.model.Nota;
import com.catalog.repository.InMemoryUnitOfWork;
import com.catalog.service.NotaService;
import com.catalog.service.interfaces.INotaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotaServiceTest {
    private INotaService notaService;

    @BeforeEach
    void setUp() {
        notaService = new NotaService(new InMemoryUnitOfWork());
    }

    @Test
    void calculeazaMedie_faraNota_returneazaZero() {
        assertEquals(0.0f, notaService.calculeazaMedie(1), 0.001f);
    }

    @Test
    void calculeazaMedie_cuNote_returneazaMediaCorecta() {
        int studentId = 42;
        notaService.insert(new Nota(studentId, 1, 8.0f));
        notaService.insert(new Nota(studentId, 2, 10.0f));
        float medie = notaService.calculeazaMedie(studentId);
        assertEquals(9.0f, medie, 0.001f);
    }

    @Test
    void getNoteStudent_returneazaDoarNoteleStudentului() {
        notaService.insert(new Nota(1, 1, 7.0f));
        notaService.insert(new Nota(2, 1, 8.0f));
        notaService.insert(new Nota(1, 2, 9.0f));
        assertEquals(2, notaService.getNoteStudent(1).size());
        assertEquals(1, notaService.getNoteStudent(2).size());
    }
}
