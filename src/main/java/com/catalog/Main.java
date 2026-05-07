package com.catalog;

import com.catalog.app.CatalogApp;
import com.catalog.repository.IUnitOfWork;
import com.catalog.repository.InMemoryUnitOfWork;

public class Main {
    public static void main(String[] args) {
        IUnitOfWork unitOfWork = new InMemoryUnitOfWork();
        CatalogApp app = new CatalogApp(unitOfWork);
        app.run();
    }
}
