package com.catalog.util;

import java.util.HashMap;
import java.util.Map;

public class IdGenerator {
    private static final Map<Class<?>, Integer> counters = new HashMap<>();

    public static int nextId(Class<?> entityClass) {
        int current = counters.getOrDefault(entityClass, 1);
        counters.put(entityClass, current + 1);
        return current;
    }

    // Etapa II: la pornire, JdbcUnitOfWork seedeaza contorul cu MAX(id) din baza de date.
    // Astfel urmatorul nextId() porneste DEASUPRA randurilor existente => fara coliziuni de cheie primara.
    public static void seed(Class<?> entityClass, int nextValue) {
        counters.put(entityClass, nextValue);
    }
}
