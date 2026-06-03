package com.catalog.service.interfaces;

import com.catalog.model.BaseEntity;

public interface IInsertable<TEntity extends BaseEntity> {
    void insert(TEntity entity);
}
