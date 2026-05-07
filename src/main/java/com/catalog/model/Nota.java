package com.catalog.model;

import java.time.LocalDate;

import com.catalog.exception.ValidationException;

public class Nota extends BaseEntity {
    private static final float VALOARE_MIN = 1.0f;
    private static final float VALOARE_MAX = 10.0f;

    private int studentId;
    private int cursId;
    private float valoare;
    private LocalDate data;

    public Nota() {
        this.data = LocalDate.now();
    }

    public Nota(int studentId, int cursId, float valoare) {
        this.studentId = studentId;
        this.cursId = cursId;
        setValoare(valoare);
        this.data = LocalDate.now();
    }

    public int getStudentId() { return studentId; }
    public int getCursId() { return cursId; }
    public float getValoare() { return valoare; }
    public LocalDate getData() { return data; }

    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setCursId(int cursId) { this.cursId = cursId; }
    public void setData(LocalDate data) { this.data = data; }

    public void setValoare(float valoare) {
        if (valoare < VALOARE_MIN || valoare > VALOARE_MAX) {
            throw new ValidationException(
                String.format("Nota trebuie sa fie intre %.1f si %.1f.", VALOARE_MIN, VALOARE_MAX)
            );
        }
        this.valoare = valoare;
    }

    @Override
    public String toString() {
        return String.format("Nota[id=%d, studentId=%d, cursId=%d, valoare=%.2f, data=%s]",
                id, studentId, cursId, valoare, data);
    }
}
