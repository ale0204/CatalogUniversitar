package com.catalog.model;

import com.catalog.model.enums.TipMaterie;

public class Materie extends BaseEntity {
    private String numeMaterie;
    private TipMaterie tipMaterie;

    public Materie() {
        this.numeMaterie = "";
        this.tipMaterie = TipMaterie.OBLIGATORIE;
    }

    public Materie(String numeMaterie, TipMaterie tipMaterie) {
        this.numeMaterie = numeMaterie;
        this.tipMaterie = tipMaterie;
    }

    public String getNumeMaterie() {
        return numeMaterie;
    }

    public void setNumeMaterie(String numeMaterie) {
        this.numeMaterie = numeMaterie;
    }

    public TipMaterie getTipMaterie() {
        return tipMaterie;
    }

    public void setTipMaterie(TipMaterie tipMaterie) {
        this.tipMaterie = tipMaterie;
    }

    @Override
    public String toString() {
        return String.format("Materie[id=%d, %s, Tip: %s]", id, numeMaterie, tipMaterie);
    }
}
