package com.catalog.service.interfaces;

import com.catalog.model.Inscriere;

public interface IInscriereService extends
        IReadable<Inscriere>,
        IInsertable<Inscriere>,
        IDeletable<Inscriere> {

    Inscriere inscrie(int studentId, int cursId);
    void retrage(int studentId, int cursId);
}
