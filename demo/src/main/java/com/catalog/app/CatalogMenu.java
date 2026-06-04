package com.catalog.app;

import java.util.List;
import java.util.Scanner;

import com.catalog.exception.CatalogException;
import com.catalog.model.Curs;
import com.catalog.model.Materie;
import com.catalog.model.Profesor;
import com.catalog.model.Student;
import com.catalog.model.enums.GradDidactic;
import com.catalog.model.enums.TipMaterie;
import com.catalog.repository.IUnitOfWork;
import com.catalog.service.CursService;
import com.catalog.service.MaterieService;
import com.catalog.service.ProfesorService;
import com.catalog.service.StudentService;
import com.catalog.service.interfaces.ICursService;
import com.catalog.service.interfaces.IMaterieService;
import com.catalog.service.interfaces.IProfesorService;
import com.catalog.service.interfaces.IStudentService;

// Meniu interactiv pentru operatii CRUD (Create/Read/Update/Delete) pe cele 4 entitati principale.
// Foloseste ACELEASI servicii ca demo-ul -- doar le apeleaza pe baza alegerilor din consola.
public class CatalogMenu {
    private final IStudentService studentService;
    private final IProfesorService profesorService;
    private final IMaterieService materieService;
    private final ICursService cursService;
    private final Scanner scanner;

    public CatalogMenu(IUnitOfWork unitOfWork, Scanner scanner) {
        this.studentService  = new StudentService(unitOfWork);
        this.profesorService = new ProfesorService(unitOfWork);
        this.materieService  = new MaterieService(unitOfWork);
        this.cursService     = new CursService(unitOfWork);
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            System.out.println("\n===== MENIU INTERACTIV (CRUD) =====");
            System.out.println("1. Studenti");
            System.out.println("2. Profesori");
            System.out.println("3. Materii");
            System.out.println("4. Cursuri");
            System.out.println("0. Inapoi");
            int opt = citesteInt("Optiune: ");
            switch (opt) {
                case 1 -> meniuStudenti();
                case 2 -> meniuProfesori();
                case 3 -> meniuMaterii();
                case 4 -> meniuCursuri();
                case 0 -> { return; }
                default -> System.out.println("Optiune invalida.");
            }
        }
    }

    // ---------- STUDENTI ----------
    private void meniuStudenti() {
        while (true) {
            int opt = afiseazaSubmeniu("STUDENTI");
            switch (opt) {
                case 1 -> afiseaza(studentService.getAll());
                case 2 -> adaugaStudent();
                case 3 -> modificaStudent();
                case 4 -> stergeStudent();
                case 0 -> { return; }
                default -> System.out.println("Optiune invalida.");
            }
        }
    }

    private void adaugaStudent() {
        try {
            String nume = citesteText("Nume: ");
            int varsta = citesteInt("Varsta: ");
            Student s = new Student(nume, varsta);
            studentService.insert(s);
            System.out.println("Adaugat: " + s);
        } catch (CatalogException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void modificaStudent() {
        Student s = studentService.getById(citesteInt("Id student: "));
        if (s == null) { System.out.println("Nu exista."); return; }
        System.out.println("Actual: " + s);
        try {
            String nume = citesteText("Nume nou (Enter = pastreaza): ");
            if (!nume.isEmpty()) s.setNume(nume);
            String varsta = citesteText("Varsta noua (Enter = pastreaza): ");
            if (!varsta.isEmpty()) s.setVarsta(Integer.parseInt(varsta));
            studentService.update(s);
            System.out.println("Modificat: " + s);
        } catch (CatalogException | NumberFormatException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void stergeStudent() {
        Student s = studentService.getById(citesteInt("Id student de sters: "));
        if (s == null) { System.out.println("Nu exista."); return; }
        try {
            studentService.delete(s);
            System.out.println("Sters.");
        } catch (CatalogException e) {
            System.out.println("Nu am putut sterge (poate are note/inscrieri legate): " + e.getMessage());
        }
    }

    // ---------- PROFESORI ----------
    private void meniuProfesori() {
        while (true) {
            int opt = afiseazaSubmeniu("PROFESORI");
            switch (opt) {
                case 1 -> afiseaza(profesorService.getAll());
                case 2 -> adaugaProfesor();
                case 3 -> modificaProfesor();
                case 4 -> stergeProfesor();
                case 0 -> { return; }
                default -> System.out.println("Optiune invalida.");
            }
        }
    }

    private void adaugaProfesor() {
        try {
            String nume = citesteText("Nume: ");
            int varsta = citesteInt("Varsta: ");
            GradDidactic grad = alegeGrad(null);
            Profesor p = new Profesor(nume, varsta, grad);
            profesorService.insert(p);
            System.out.println("Adaugat: " + p);
        } catch (CatalogException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void modificaProfesor() {
        Profesor p = profesorService.getById(citesteInt("Id profesor: "));
        if (p == null) { System.out.println("Nu exista."); return; }
        System.out.println("Actual: " + p);
        try {
            String nume = citesteText("Nume nou (Enter = pastreaza): ");
            if (!nume.isEmpty()) p.setNume(nume);
            String varsta = citesteText("Varsta noua (Enter = pastreaza): ");
            if (!varsta.isEmpty()) p.setVarsta(Integer.parseInt(varsta));
            p.setGradDidactic(alegeGrad(p.getGradDidactic()));
            profesorService.update(p);
            System.out.println("Modificat: " + p);
        } catch (CatalogException | NumberFormatException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void stergeProfesor() {
        Profesor p = profesorService.getById(citesteInt("Id profesor de sters: "));
        if (p == null) { System.out.println("Nu exista."); return; }
        try {
            profesorService.delete(p);
            System.out.println("Sters.");
        } catch (CatalogException e) {
            System.out.println("Nu am putut sterge (poate preda cursuri): " + e.getMessage());
        }
    }

    // ---------- MATERII ----------
    private void meniuMaterii() {
        while (true) {
            int opt = afiseazaSubmeniu("MATERII");
            switch (opt) {
                case 1 -> afiseaza(materieService.getAll());
                case 2 -> adaugaMaterie();
                case 3 -> modificaMaterie();
                case 4 -> stergeMaterie();
                case 0 -> { return; }
                default -> System.out.println("Optiune invalida.");
            }
        }
    }

    private void adaugaMaterie() {
        try {
            String nume = citesteText("Nume materie: ");
            TipMaterie tip = alegeTip(null);
            Materie m = new Materie(nume, tip);
            materieService.insert(m);
            System.out.println("Adaugat: " + m);
        } catch (CatalogException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void modificaMaterie() {
        Materie m = materieService.getById(citesteInt("Id materie: "));
        if (m == null) { System.out.println("Nu exista."); return; }
        System.out.println("Actual: " + m);
        try {
            String nume = citesteText("Nume nou (Enter = pastreaza): ");
            if (!nume.isEmpty()) m.setNumeMaterie(nume);
            m.setTipMaterie(alegeTip(m.getTipMaterie()));
            materieService.update(m);
            System.out.println("Modificat: " + m);
        } catch (CatalogException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void stergeMaterie() {
        Materie m = materieService.getById(citesteInt("Id materie de sters: "));
        if (m == null) { System.out.println("Nu exista."); return; }
        try {
            materieService.delete(m);
            System.out.println("Sters.");
        } catch (CatalogException e) {
            System.out.println("Nu am putut sterge (poate e folosita de cursuri): " + e.getMessage());
        }
    }

    // ---------- CURSURI ----------
    private void meniuCursuri() {
        while (true) {
            int opt = afiseazaSubmeniu("CURSURI");
            switch (opt) {
                case 1 -> afiseaza(cursService.getAll());
                case 2 -> adaugaCurs();
                case 3 -> modificaCurs();
                case 4 -> stergeCurs();
                case 0 -> { return; }
                default -> System.out.println("Optiune invalida.");
            }
        }
    }

    private void adaugaCurs() {
        System.out.println("-- Materii disponibile --");
        materieService.getAll().forEach(m -> System.out.printf("  id=%d : %s%n", m.getId(), m.getNumeMaterie()));
        System.out.println("-- Profesori disponibili --");
        profesorService.getAll().forEach(p -> System.out.printf("  id=%d : %s%n", p.getId(), p.getNume()));
        try {
            int materieId  = citesteInt("Id materie: ");
            int profesorId = citesteInt("Id profesor: ");
            String an      = citesteText("An universitar (ex 2025-2026): ");
            int semestru   = citesteInt("Semestru: ");
            int max        = citesteInt("Numar maxim studenti: ");
            Curs c = new Curs(materieId, profesorId, an, semestru, max);
            cursService.insert(c);
            System.out.println("Adaugat: " + c);
        } catch (CatalogException e) {
            System.out.println("Eroare (poate id materie/profesor inexistent): " + e.getMessage());
        }
    }

    private void modificaCurs() {
        Curs c = cursService.getById(citesteInt("Id curs: "));
        if (c == null) { System.out.println("Nu exista."); return; }
        System.out.println("Actual: " + c);
        try {
            String an = citesteText("An nou (Enter = pastreaza): ");
            if (!an.isEmpty()) c.setAnUniversitar(an);
            String sem = citesteText("Semestru nou (Enter = pastreaza): ");
            if (!sem.isEmpty()) c.setSemestru(Integer.parseInt(sem));
            String max = citesteText("Max studenti nou (Enter = pastreaza): ");
            if (!max.isEmpty()) c.setMaxStudenti(Integer.parseInt(max));
            cursService.update(c);
            System.out.println("Modificat: " + c);
        } catch (CatalogException | NumberFormatException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void stergeCurs() {
        Curs c = cursService.getById(citesteInt("Id curs de sters: "));
        if (c == null) { System.out.println("Nu exista."); return; }
        try {
            cursService.delete(c);
            System.out.println("Sters.");
        } catch (CatalogException e) {
            System.out.println("Nu am putut sterge (poate are note/inscrieri legate): " + e.getMessage());
        }
    }

    // ---------- AJUTOARE ----------
    private int afiseazaSubmeniu(String titlu) {
        System.out.println("\n--- " + titlu + " ---");
        System.out.println("1. Listeaza toti");
        System.out.println("2. Adauga");
        System.out.println("3. Modifica");
        System.out.println("4. Sterge");
        System.out.println("0. Inapoi");
        return citesteInt("Optiune: ");
    }

    private void afiseaza(List<?> lista) {
        if (lista.isEmpty()) {
            System.out.println("(lista goala)");
            return;
        }
        lista.forEach(System.out::println);
    }

    private GradDidactic alegeGrad(GradDidactic curent) {
        GradDidactic[] valori = GradDidactic.values();
        System.out.println("Grad didactic:");
        for (int i = 0; i < valori.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, valori[i]);
        }
        String prompt = (curent == null) ? "Optiune: " : "Optiune (Enter = pastreaza " + curent + "): ";
        System.out.print(prompt);
        String linie = scanner.nextLine().trim();
        if (linie.isEmpty() && curent != null) return curent;
        try {
            int idx = Integer.parseInt(linie);
            if (idx >= 1 && idx <= valori.length) return valori[idx - 1];
        } catch (NumberFormatException ignored) { }
        System.out.println("Optiune invalida, folosesc " + (curent != null ? curent : GradDidactic.NESPECIFICAT) + ".");
        return (curent != null) ? curent : GradDidactic.NESPECIFICAT;
    }

    private TipMaterie alegeTip(TipMaterie curent) {
        TipMaterie[] valori = TipMaterie.values();
        System.out.println("Tip materie:");
        for (int i = 0; i < valori.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, valori[i]);
        }
        String prompt = (curent == null) ? "Optiune: " : "Optiune (Enter = pastreaza " + curent + "): ";
        System.out.print(prompt);
        String linie = scanner.nextLine().trim();
        if (linie.isEmpty() && curent != null) return curent;
        try {
            int idx = Integer.parseInt(linie);
            if (idx >= 1 && idx <= valori.length) return valori[idx - 1];
        } catch (NumberFormatException ignored) { }
        System.out.println("Optiune invalida, folosesc " + (curent != null ? curent : TipMaterie.OBLIGATORIE) + ".");
        return (curent != null) ? curent : TipMaterie.OBLIGATORIE;
    }

    private int citesteInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linie = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linie);
            } catch (NumberFormatException e) {
                System.out.println("Introdu un numar valid.");
            }
        }
    }

    private String citesteText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
