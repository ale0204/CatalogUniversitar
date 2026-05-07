package com.catalog.model;

import java.time.LocalDate;

import com.catalog.model.enums.StatusInscriere;

public class Inscriere extends BaseEntity {
    private int studentId;
    private int cursId;
    private StatusInscriere status;
    private LocalDate dataInscriere;

    public Inscriere() {
        this.status = StatusInscriere.ACTIV;
        this.dataInscriere = LocalDate.now();
    }

    public Inscriere(int studentId, int cursId) {
        this.studentId = studentId;
        this.cursId = cursId;
        this.status = StatusInscriere.ACTIV;
        this.dataInscriere = LocalDate.now();
    }

    public int getStudentId() { return studentId; }
    public int getCursId() { return cursId; }
    public StatusInscriere getStatus() { return status; }
    public LocalDate getDataInscriere() { return dataInscriere; }

    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setCursId(int cursId) { this.cursId = cursId; }
    public void setStatus(StatusInscriere status) { this.status = status; }
    public void setDataInscriere(LocalDate dataInscriere) { this.dataInscriere = dataInscriere; }

    @Override
    public String toString() {
        return String.format("Inscriere[id=%d, studentId=%d, cursId=%d, status=%s, data=%s]",
                id, studentId, cursId, status, dataInscriere);
    }
}
