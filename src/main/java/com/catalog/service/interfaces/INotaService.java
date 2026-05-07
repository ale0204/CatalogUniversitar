package com.catalog.service.interfaces;

import java.util.List;

import com.catalog.model.Nota;

// Nota nu expune update sau delete -- o nota inregistrata e imutabila din perspectiva UI.
// update() exista in BaseService daca e nevoie intern, dar nu e parte din contract.
public interface INotaService extends
        IReadable<Nota>,
        IInsertable<Nota> {

    float calculeazaMedie(int studentId);
    List<Nota> getNoteStudent(int studentId);
}
