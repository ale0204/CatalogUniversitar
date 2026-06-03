package com.catalog.model;

public class Curs extends BaseEntity {
    private int materieId;
    private int profesorId;
    private String anUniversitar;
    private int semestru;
    private int maxStudenti;

    public Curs() {
    }

    public Curs(int materieId, int profesorId, String anUniversitar, int semestru, int maxStudenti) {
        this.materieId = materieId;
        this.profesorId = profesorId;
        this.anUniversitar = anUniversitar;
        this.semestru = semestru;
        this.maxStudenti = maxStudenti;
    }

    public int getMaterieId() { return materieId; }
    public int getProfesorId() { return profesorId; }
    public String getAnUniversitar() { return anUniversitar; }
    public int getSemestru() { return semestru; }
    public int getMaxStudenti() { return maxStudenti; }

    public void setMaterieId(int materieId) { this.materieId = materieId; }
    public void setProfesorId(int profesorId) { this.profesorId = profesorId; }
    public void setAnUniversitar(String anUniversitar) { this.anUniversitar = anUniversitar; }
    public void setSemestru(int semestru) { this.semestru = semestru; }
    public void setMaxStudenti(int maxStudenti) { this.maxStudenti = maxStudenti; }

    @Override
    public String toString() {
        return String.format("Curs[id=%d, materieId=%d, profesorId=%d, %s sem%d, max=%d]",
                id, materieId, profesorId, anUniversitar, semestru, maxStudenti);
    }
}
