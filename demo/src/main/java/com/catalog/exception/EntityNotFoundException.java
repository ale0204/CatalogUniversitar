package com.catalog.exception;

public class EntityNotFoundException extends CatalogException {
    public EntityNotFoundException(String entityType, int id) {
        super(String.format("%s cu id=%d nu a fost gasit.", entityType, id));
    }
}
