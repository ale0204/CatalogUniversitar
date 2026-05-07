package com.catalog.repository;

import java.util.List;

import com.catalog.model.BaseEntity;

public interface IRepository<TEntity extends BaseEntity> {
    TEntity getById(int id);
    List<TEntity> getAll();
    void insert(TEntity entity);
    void update(TEntity entity);
    void delete(TEntity entity);
}
