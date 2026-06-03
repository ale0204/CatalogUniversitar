package com.catalog.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.catalog.exception.CatalogException;

// Singleton: toate actiunile scriu in ACELASI fisier CSV.
// Structura ceruta de enunt: nume_actiune, timestamp
public class AuditService {
    private static final String CSV_FILE = "audit.csv";
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static AuditService instance;

    private AuditService() {
        // La prima initializare, daca fisierul nu exista, scriem antetul (header) CSV.
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            scrieLinie("nume_actiune,timestamp");
        }
    }

    public static synchronized AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    // Inregistreaza o actiune: adauga o linie "nume_actiune,timestamp" la finalul CSV-ului.
    public synchronized void log(String numeActiune) {
        String timestamp = LocalDateTime.now().format(FORMAT);
        scrieLinie(numeActiune + "," + timestamp);
    }

    // append = true => nu suprascrie continutul, adauga la final.
    private void scrieLinie(String linie) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE, true))) {
            writer.println(linie);
        } catch (IOException e) {
            throw new CatalogException("Eroare la scrierea in " + CSV_FILE + ": " + e.getMessage());
        }
    }
}
