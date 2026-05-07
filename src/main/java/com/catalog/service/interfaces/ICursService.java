package com.catalog.service.interfaces;

import java.util.List;

import com.catalog.model.Curs;
import com.catalog.model.Student;

public interface ICursService extends
        IReadable<Curs>,
        IInsertable<Curs>,
        IUpdatable<Curs>,
        IDeletable<Curs> {

    List<Student> getStudentiInscrisi(int cursId);
}
