package com.catalog;

import java.util.Scanner;

import com.catalog.app.CatalogApp;
import com.catalog.app.CatalogMenu;
import com.catalog.repository.IUnitOfWork;
import com.catalog.repository.JdbcUnitOfWork;

public class Main {
    public static void main(String[] args) {

        IUnitOfWork unitOfWork = new JdbcUnitOfWork();

        // Un singur Scanner pentru tot programul (doua Scanner-e pe System.in dau probleme).
        Scanner scanner = new Scanner(System.in);

        System.out.print("Golesc baza de date inainte de rulare? (y/n): ");
        String raspuns = scanner.nextLine().trim().toLowerCase();
        if ((raspuns.equals("y") || raspuns.equals("yes") || raspuns.equals("da"))
                && unitOfWork instanceof JdbcUnitOfWork jdbc) {
            jdbc.curataToateTabelele();
            System.out.println("[Baza de date golita. Pornesc de la zero.]");
        }

        boolean ruleaza = true;
        while (ruleaza) {
            System.out.println("\n========== CATALOG UNIVERSITAR ==========");
            System.out.println("1. Ruleaza demo-ul automat (cele 12 actiuni)");
            System.out.println("2. Meniu interactiv (CRUD: listeaza/adauga/modifica/sterge)");
            System.out.println("0. Iesire");
            System.out.print("Optiune: ");
            String optiune = scanner.nextLine().trim();
            switch (optiune) {
                case "1" -> new CatalogApp(unitOfWork).run();
                case "2" -> new CatalogMenu(unitOfWork, scanner).run();
                case "0" -> ruleaza = false;
                default  -> System.out.println("Optiune invalida.");
            }
        }

        scanner.close();
        System.out.println("La revedere!");
    }
}
