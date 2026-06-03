package com.catalog.service.interfaces;

import java.util.TreeSet;

import com.catalog.model.Student;

public interface IStudentService extends
        IReadable<Student>,
        IInsertable<Student>,
        IUpdatable<Student>,
        IDeletable<Student> {

    TreeSet<Student> getStudentiOrdonatiDupaMedie();
}
