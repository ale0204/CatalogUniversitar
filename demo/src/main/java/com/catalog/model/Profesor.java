package com.catalog.model;

import java.util.HashSet;

import com.catalog.model.enums.GradDidactic;

public class Profesor extends Person {
    private HashSet<Materie> materiiPredate;
    private GradDidactic gradDidactic;

    public Profesor() {
        this.materiiPredate = new HashSet<>();
        this.gradDidactic = GradDidactic.NESPECIFICAT;
    }

    public Profesor(GradDidactic gradDidactic) {
        this.materiiPredate = new HashSet<>();
        this.gradDidactic = gradDidactic;
    }

    public Profesor(String nume, int varsta, GradDidactic gradDidactic) {
        super(nume, varsta);
        this.materiiPredate = new HashSet<>();
        this.gradDidactic = gradDidactic;
    }

    public HashSet<Materie> getMateriiPredate() {
        return materiiPredate;
    }

    public GradDidactic getGradDidactic() {
        return gradDidactic;
    }

    public void setGradDidactic(GradDidactic gradDidactic) {
        this.gradDidactic = gradDidactic;
    }

    public void adaugaMaterie(Materie materie) {
        materiiPredate.add(materie);
    }

    @Override
    public String toString() {
        return String.format("Profesor[id=%d, %s, Grad: %s]", id, super.toString(), gradDidactic);
    }
}
