package com.catalog.exception;

public class DuplicateEntityException extends CatalogException {
    public DuplicateEntityException(String entityType, int id) {
        super(String.format("%s cu id=%d exista deja.", entityType, id));
    }
}
