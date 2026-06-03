package com.catalog.app;

import java.util.TreeSet;

import com.catalog.exception.BusinessRuleException;
import com.catalog.model.Curs;
import com.catalog.model.Materie;
import com.catalog.model.Nota;
import com.catalog.model.Profesor;
import com.catalog.model.Student;
import com.catalog.model.enums.GradDidactic;
import com.catalog.model.enums.TipMaterie;
import com.catalog.repository.IUnitOfWork;
import com.catalog.service.CursService;
import com.catalog.service.InscriereService;
import com.catalog.service.MaterieService;
import com.catalog.service.NotaService;
import com.catalog.service.ProfesorService;
import com.catalog.service.StudentService;
import com.catalog.service.interfaces.ICursService;
import com.catalog.service.interfaces.IInscriereService;
import com.catalog.service.interfaces.IMaterieService;
import com.catalog.service.interfaces.INotaService;
import com.catalog.service.interfaces.IProfesorService;
import com.catalog.service.interfaces.IStudentService;

public class CatalogApp {
    private final IStudentService studentService;
    private final IProfesorService profesorService;
    private final IMaterieService materieService;
    private final INotaService notaService;
    private final ICursService cursService;
    private final IInscriereService inscriereService;

    public CatalogApp(IUnitOfWork unitOfWork) {
        this.studentService   = new StudentService(unitOfWork);
        this.profesorService  = new ProfesorService(unitOfWork);
        this.materieService   = new MaterieService(unitOfWork);
        this.notaService      = new NotaService(unitOfWork);
        this.cursService      = new CursService(unitOfWork);
        this.inscriereService = new InscriereService(unitOfWork);
    }

    public void run() {
        System.out.println("========================================");
        System.out.println("     SISTEM CATALOG UNIVERSITAR");
        System.out.println("========================================\n");

        separator("1. Adaugare studenti");
        Student ion = new Student("Ion Popescu", 20);
        Student maria = new Student("Maria Ionescu", 21);
        Student alex = new Student("Alexandru Dumitrescu", 19);
        studentService.insert(ion);
        studentService.insert(maria);
        studentService.insert(alex);
        studentService.getAll().forEach(System.out::println);

        separator("2. Adaugare profesori");
        Profesor popa = new Profesor("Elena Popa", 45, GradDidactic.CONFERENTIAR);
        Profesor stan = new Profesor("Mihai Stan", 38, GradDidactic.LECTOR);
        profesorService.insert(popa);
        profesorService.insert(stan);
        profesorService.getAll().forEach(System.out::println);

        separator("3. Adaugare materii");
        Materie poo = new Materie("Programare Orientata Obiecte", TipMaterie.OBLIGATORIE);
        Materie bd  = new Materie("Baze de Date", TipMaterie.OBLIGATORIE);
        Materie ia  = new Materie("Inteligenta Artificiala", TipMaterie.OPTIONALA);
        materieService.insert(poo);
        materieService.insert(bd);
        materieService.insert(ia);
        materieService.getAll().forEach(System.out::println);

        separator("4. Creare cursuri");
        Curs cursPoo = new Curs(poo.getId(), popa.getId(), "2025-2026", 2, 3);
        Curs cursBd  = new Curs(bd.getId(), stan.getId(), "2025-2026", 2, 2);
        cursService.insert(cursPoo);
        cursService.insert(cursBd);
        cursService.getAll().forEach(System.out::println);

        separator("5. Inscriere studenti la cursuri");
        inscriereService.inscrie(ion.getId(), cursPoo.getId());
        inscriereService.inscrie(maria.getId(), cursPoo.getId());
        inscriereService.inscrie(alex.getId(), cursPoo.getId());
        inscriereService.inscrie(ion.getId(), cursBd.getId());
        inscriereService.inscrie(maria.getId(), cursBd.getId());
        inscriereService.getAll().forEach(System.out::println);

        System.out.println("\n[Test] Inscriere la curs plin:");
        Student andrei = new Student("Andrei Muresan", 22);
        studentService.insert(andrei);
        try {
            inscriereService.inscrie(andrei.getId(), cursBd.getId());
        } catch (BusinessRuleException e) {
            System.out.println("BusinessRuleException: " + e.getMessage());
        }

        separator("6. Adaugare note");
        notaService.insert(new Nota(ion.getId(), cursPoo.getId(), 9.5f));
        notaService.insert(new Nota(maria.getId(), cursPoo.getId(), 8.0f));
        notaService.insert(new Nota(alex.getId(), cursPoo.getId(), 7.5f));
        notaService.insert(new Nota(ion.getId(), cursBd.getId(), 10.0f));
        notaService.insert(new Nota(maria.getId(), cursBd.getId(), 6.5f));
        notaService.getAll().forEach(System.out::println);

        separator("7. Calcul medii studenti");
        for (Student s : studentService.getAll()) {
            System.out.printf("%s -> medie: %.2f%n", s.getNume(), notaService.calculeazaMedie(s.getId()));
        }

        separator("8. Studenti ordonati dupa medie (TreeSet)");
        TreeSet<Student> sortati = studentService.getStudentiOrdonatiDupaMedie();
        sortati.forEach(s -> System.out.printf("%s -> %.2f%n",
                s.getNume(), notaService.calculeazaMedie(s.getId())));

        separator("9. Materiile predate de " + popa.getNume());
        profesorService.getMateriiPredate(popa.getId()).forEach(System.out::println);

        separator("10. Studenti inscrisi la cursul POO");
        cursService.getStudentiInscrisi(cursPoo.getId()).forEach(System.out::println);

        separator("11. Retragere " + alex.getNume() + " din cursul POO");
        inscriereService.retrage(alex.getId(), cursPoo.getId());
        inscriereService.getAll().stream()
                .filter(i -> i.getCursId() == cursPoo.getId())
                .forEach(System.out::println);

        separator("12. Notele lui " + ion.getNume());
        notaService.getNoteStudent(ion.getId()).forEach(System.out::println);

        System.out.println("\n========================================");
        System.out.println("     DEMO FINALIZAT CU SUCCES!");
        System.out.println("========================================");
    }

    private void separator(String titlu) {
        System.out.println("\n--- " + titlu + " " + "-".repeat(Math.max(0, 40 - titlu.length())));
    }
}
