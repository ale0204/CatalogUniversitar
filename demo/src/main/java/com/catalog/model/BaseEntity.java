package com.catalog.model;

import java.util.Objects;

import com.catalog.util.IdGenerator;

public abstract class BaseEntity {
    protected int id;

    protected BaseEntity() {
        this.id = IdGenerator.nextId(this.getClass());
    }

    public int getId() {
        return id;
    }

    // Etapa II: necesar pentru a seta id-ul citit din baza de date pe obiectul reconstruit.
    // La construirea din ResultSet, constructorul atribuie un id nou (via IdGenerator);
    // mapper-ul il suprascrie cu id-ul real din DB prin aceasta metoda.
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseEntity other = (BaseEntity) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }
}
