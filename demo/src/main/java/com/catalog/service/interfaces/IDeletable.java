package com.catalog.service.interfaces;

import com.catalog.model.BaseEntity;

public interface IDeletable<TEntity extends BaseEntity> {
    void delete(TEntity entity);
}
