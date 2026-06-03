# Catalog Universitar - Proiect POO

**Tema**: Catalog universitar (student, materie, profesor)  
**Etapa I**: Implementare in-memory cu Java  
**Etapa II**: Persistenta JDBC + Audit CSV

---

## Setup dupa clonare

1. Ai nevoie de **Java 17** si **PostgreSQL** pornit.
2. Creeaza baza de date `catalog`, apoi ruleaza schema: `demo/schema.sql`.
3. Copiaza `demo/database.properties.example` in `demo/database.properties` si pune-ti parola.
4. Deschide proiectul `demo/` in VS Code (extensia Java descarca driverul din `pom.xml`) si ruleaza `Main.java`.

Detalii complete despre Etapa II in **[ETAPA2.md](ETAPA2.md)**.

---

## Tipuri de obiecte (11 total)

| # | Tip | Pachet | Descriere |
|---|-----|--------|-----------|
| 1 | `BaseEntity` (abstract) | `model` | Baza ierarhiei, contine `id` auto-generat |
| 2 | `Person` (abstract) | `model` | Extinde BaseEntity, adauga `nume` si `varsta` |
| 3 | `Student` | `model` | Extinde Person |
| 4 | `Profesor` | `model` | Extinde Person, are `gradDidactic` si `HashSet<Materie>` |
| 5 | `Materie` | `model` | Extinde BaseEntity, are `numeMaterie` si `tipMaterie` |
| 6 | `Nota` | `model` | Extinde BaseEntity, leaga `studentId` + `cursId` + `valoare` |
| 7 | `Curs` | `model` | Extinde BaseEntity, leaga `materieId` + `profesorId`, capacitate |
| 8 | `Inscriere` | `model` | Extinde BaseEntity, leaga `studentId` + `cursId` + `status` |
| 9 | `GradDidactic` (enum) | `model.enums` | NESPECIFICAT, COLABORATOR, ASISTENT, LECTOR, CONFERENTIAR, PROFESOR |
| 10 | `StatusInscriere` (enum) | `model.enums` | ACTIV, RETRAS, FINALIZAT |
| 11 | `TipMaterie` (enum) | `model.enums` | OBLIGATORIE, OPTIONALA, FACULTATIVA |

---

## Actiuni / Interogari (12 total)

| # | Actiune | Serviciu | Descriere |
|---|---------|----------|-----------|
| 1 | `adaugaStudent` | `StudentService` | Creeaza si salveaza un student nou |
| 2 | `adaugaProfesor` | `ProfesorService` | Creeaza si salveaza un profesor nou |
| 3 | `adaugaMaterie` | `MaterieService` | Creeaza si salveaza o materie noua |
| 4 | `creeazaCurs` | `CursService` | Creeaza un curs (leaga materie + profesor, seteaza capacitate maxima) |
| 5 | `inscriereStudentLaCurs` | `InscriereService` | Inscrie student la curs (verifica daca mai sunt locuri) |
| 6 | `adaugaNota` | `NotaService` | Adauga nota unui student la un curs (validata 1-10) |
| 7 | `calculeazaMedieStudent` | `NotaService` | Calculeaza media aritmetica din toate notele unui student |
| 8 | `afiseazaStudentiOrdonatiDupaMedie` | `StudentService` | Returneaza `TreeSet<Student>` sortat descrescator dupa medie |
| 9 | `afiseazaMateriiProfesor` | `ProfesorService` | Lista materiilor predate de un profesor (prin cursurile sale) |
| 10 | `afiseazaStudentiInscrisiLaCurs` | `CursService` | Lista studentilor activi inscrisi la un curs |
| 11 | `retragereStudentDinCurs` | `InscriereService` | Schimba statusul inscrierii din ACTIV in RETRAS |
| 12 | `afiseazaNoteleStudentului` | `NotaService` | Lista tuturor notelor unui student |

---

## Structura proiect

```
src/main/java/com/catalog/
  Main.java                    -- 3 linii: creeaza UoW, creeaza CatalogApp, run()
  app/
    CatalogApp.java            -- orchestrator: demonstreaza toate cele 12 actiuni
  model/
    BaseEntity.java, Person.java, Student.java, Profesor.java,
    Materie.java, Nota.java, Curs.java, Inscriere.java
    enums/ GradDidactic.java, StatusInscriere.java, TipMaterie.java
  repository/
    IRepository.java, InMemoryRepository.java
    IUnitOfWork.java, InMemoryUnitOfWork.java
  service/
    IService.java (generic)
    IStudentService.java + StudentService.java
    IProfesorService.java + ProfesorService.java
    IMaterieService.java + MaterieService.java
    INotaService.java + NotaService.java
    ICursService.java + CursService.java
    IInscriereService.java + InscriereService.java
  exception/
    CatalogException.java (baza), EntityNotFoundException.java,
    DuplicateEntityException.java, ValidationException.java,
    BusinessRuleException.java
  util/
    IdGenerator.java
```

---

## Colectii utilizate

- **`ArrayList<T>`** - in `InMemoryRepository` (stocare principala)
- **`HashSet<Materie>`** - in `Profesor.materiiPredate` (fara duplicate)
- **`TreeSet<Student>`** - in `StudentService.getStudentiOrdonatiDupaMedie()` (**colectie sortata**)

---

## Etapa II — Persistenta JDBC + Audit (IMPLEMENTAT)

Etapa II adauga persistenta in **PostgreSQL** prin **JDBC** + un serviciu de **audit CSV**.
Detalii complete, pas cu pas, in **[ETAPA2.md](ETAPA2.md)**.

Pe scurt, s-au adaugat:
- `DatabaseConnection` (singleton) — conexiunea JDBC, citeste `database.properties`
- `EntityMapper<T>` + `JdbcRepository<T>` (generic) + 6 mappere — CRUD cu SQL pt toate entitatile
- `JdbcUnitOfWork` implements `IUnitOfWork` — leaga repo-urile JDBC, seedeaza `IdGenerator`
- `AuditService` (singleton) — scrie `audit.csv`: `nume_actiune,timestamp`
- O singura modificare in `Main.java`: `new InMemoryUnitOfWork()` → `new JdbcUnitOfWork()`

Serviciile (in afara de apelurile de audit), entitatile si orchestratorul **nu s-au refactorizat**.

### Rulare Etapa II
1. PostgreSQL pornit, baza `catalog` creata, schema rulata (`demo/schema.sql`).
2. Completeaza `demo/database.properties` cu credentialele tale.
3. Ruleaza `Main` din VS Code (driverul vine din `pom.xml`) sau din linia de comanda
   (vezi sectiunea din [ETAPA2.md](ETAPA2.md)).
