package com.catalog.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.catalog.exception.EntityNotFoundException;
import com.catalog.model.BaseEntity;

public class InMemoryRepository<TEntity extends BaseEntity> implements IRepository<TEntity> {
    private ArrayList<TEntity> repository;

    public InMemoryRepository() {
        repository = new ArrayList<>();
    }

    @Override
    public TEntity getById(int id) {
        return repository.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<TEntity> getAll() {
        return repository;
    }

    @Override
    public void insert(TEntity entity) {
        repository.add(entity);
    }

    @Override
    public void update(TEntity entity) {
        int idx = IntStream.range(0, repository.size())
                .filter(i -> repository.get(i).getId() == entity.getId())
                .findFirst()
                .orElse(-1);

        if (idx == -1) {
            throw new EntityNotFoundException(entity.getClass().getSimpleName(), entity.getId());
        }

        repository.set(idx, entity);
    }

    @Override
    public void delete(TEntity entity) {
        repository.removeIf(e -> e.getId() == entity.getId());
    }
}
