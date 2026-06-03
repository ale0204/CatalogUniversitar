package com.catalog;

import java.util.Scanner;

import com.catalog.app.CatalogApp;
import com.catalog.repository.IUnitOfWork;
import com.catalog.repository.JdbcUnitOfWork;

public class Main {
    public static void main(String[] args) {
        // Etapa I: new InMemoryUnitOfWork();  (datele traiau doar in memorie)
        // Etapa II: new JdbcUnitOfWork();     (datele se persista in PostgreSQL)
        IUnitOfWork unitOfWork = new JdbcUnitOfWork();

        // Optional (nu e cerut de enunt): intrebam daca golim baza inainte de rulare,
        // ca sa nu se acumuleze date la fiecare rulare. Golirea are sens doar pe varianta JDBC.
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Golesc baza de date inainte de rulare? (y/n): ");
            String raspuns = scanner.nextLine().trim().toLowerCase();
            if ((raspuns.equals("y") || raspuns.equals("yes") || raspuns.equals("da"))
                    && unitOfWork instanceof JdbcUnitOfWork jdbc) {
                jdbc.curataToateTabelele();
                System.out.println("[Baza de date golita. Pornesc de la zero.]");
            }
        }

        CatalogApp app = new CatalogApp(unitOfWork);
        app.run();
    }
}
