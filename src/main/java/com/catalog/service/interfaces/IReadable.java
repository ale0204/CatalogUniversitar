package com.catalog.service.interfaces;

import java.util.List;

import com.catalog.model.BaseEntity;

public interface IReadable<TEntity extends BaseEntity> {
    TEntity getById(int id);
    List<TEntity> getAll();
}
