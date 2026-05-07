package com.catalog;

import com.catalog.exception.ValidationException;
import com.catalog.model.Nota;
import com.catalog.model.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    @Test
    void setVarsta_varstaInvalida_aruncarValidationException() {
        Student s = new Student();
        assertThrows(ValidationException.class, () -> s.setVarsta(-1));
        assertThrows(ValidationException.class, () -> s.setVarsta(101));
    }

    @Test
    void setVarsta_varstaValida_nuArunca() {
        Student s = new Student();
        assertDoesNotThrow(() -> s.setVarsta(20));
        assertEquals(20, s.getVarsta());
    }

    @Test
    void nota_valoraInvalida_aruncarValidationException() {
        assertThrows(ValidationException.class, () -> new Nota(1, 1, 0.5f));
        assertThrows(ValidationException.class, () -> new Nota(1, 1, 10.5f));
    }

    @Test
    void nota_valoraValida_nuArunca() {
        assertDoesNotThrow(() -> new Nota(1, 1, 5.0f));
        assertDoesNotThrow(() -> new Nota(1, 1, 10.0f));
    }
}
