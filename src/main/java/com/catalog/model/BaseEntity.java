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
