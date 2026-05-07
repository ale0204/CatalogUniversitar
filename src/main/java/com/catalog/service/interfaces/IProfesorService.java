package com.catalog.service.interfaces;

import java.util.List;

import com.catalog.model.Materie;
import com.catalog.model.Profesor;

public interface IProfesorService extends
        IReadable<Profesor>,
        IInsertable<Profesor>,
        IUpdatable<Profesor>,
        IDeletable<Profesor> {

    List<Materie> getMateriiPredate(int profesorId);
}
