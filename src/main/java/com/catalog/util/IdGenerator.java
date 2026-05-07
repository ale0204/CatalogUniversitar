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
}
