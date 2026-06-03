package com.catalog.model;

import com.catalog.exception.ValidationException;

public abstract class Person extends BaseEntity {
    private static final int VARSTA_MIN = 0;
    private static final int VARSTA_MAX = 100;

    protected String nume;
    protected int varsta;

    protected Person() {
        nume = "";
        varsta = 0;
    }

    protected Person(String nume, int varsta) {
        setNume(nume);
        setVarsta(varsta);
    }

    public int getVarsta() {
        return varsta;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    // ValidationException e unchecked (extends RuntimeException),
    // deci nu e nevoie de throws in semnatura -- caller-ul decide daca o prinde sau nu.
    public void setVarsta(int varsta) {
        if (varsta < VARSTA_MIN || varsta > VARSTA_MAX) {
            throw new ValidationException(
                String.format("Varsta trebuie sa fie intre %d si %d.", VARSTA_MIN, VARSTA_MAX)
            );
        }
        this.varsta = varsta;
    }

    @Override
    public String toString() {
        return String.format("Nume: %s, Varsta: %d", nume, varsta);
    }
}
