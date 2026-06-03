package com.catalog.service.interfaces;

import com.catalog.model.BaseEntity;

public interface IUpdatable<TEntity extends BaseEntity> {
    void update(TEntity entity);
}
