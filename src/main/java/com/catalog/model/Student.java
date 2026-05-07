package com.catalog.model;

// Medie NU e camp -- se calculeaza dinamic din note via NotaService.
public class Student extends Person {

    public Student() {
    }

    public Student(String nume, int varsta) {
        super(nume, varsta);
    }

    @Override
    public String toString() {
        return String.format("Student[id=%d, %s]", id, super.toString());
    }
}
