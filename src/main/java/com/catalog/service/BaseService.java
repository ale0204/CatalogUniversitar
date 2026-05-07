package com.catalog.service;

import java.util.List;

import com.catalog.model.BaseEntity;
import com.catalog.repository.IRepository;

// Implementare generica a operatiilor CRUD.
// Subclasele extind BaseService<T> si primesc repo-ul principal prin super().
// Repo-urile extra (cross-entity) se injecteaza direct in subclasa.
public abstract class BaseService<TEntity extends BaseEntity> {
    protected final IRepository<TEntity> repository;

    protected BaseService(IRepository<TEntity> repository) {
        this.repository = repository;
    }

    public TEntity getById(int id) {
        return repository.getById(id);
    }

    public List<TEntity> getAll() {
        return repository.getAll();
    }

    public void insert(TEntity entity) {
        repository.insert(entity);
    }

    public void update(TEntity entity) {
        repository.update(entity);
    }

    public void delete(TEntity entity) {
        repository.delete(entity);
    }
}
