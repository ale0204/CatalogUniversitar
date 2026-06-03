# Catalog Universitar — Documentatie Arhitectura

## Cuprins
1. [Flow general](#1-flow-general)
2. [Structura pachete](#2-structura-pachete)
3. [Layere si responsabilitati](#3-layere-si-responsabilitati)
4. [Ierarhii de mostenire](#4-ierarhii-de-mostenire)
5. [Patterns folosite](#5-patterns-folosite)
6. [Decizii de design](#6-decizii-de-design)
7. [Etapa II — ce se adauga](#7-etapa-ii--ce-se-adauga)
8. [Q&A — intrebari ipotetice](#8-qa--intrebari-ipotetice)

---

## 1. Flow general

```
Main.java
  └── creeaza InMemoryUnitOfWork
  └── creeaza CatalogApp(unitOfWork)
  └── app.run()
        └── creeaza toate serviciile (primesc IUnitOfWork)
        └── demonstreaza 12 actiuni secvential
```

Regula de baza: **Main nu stie nimic despre repo-uri sau servicii concrete.**
Stie doar `IUnitOfWork` si `CatalogApp`. Atat.

Cum ajung datele de la Main la baza de date (in-memory acum, JDBC in Etapa II):

```
CatalogApp
  └── IStudentService (implementat de StudentService)
        └── BaseService<Student>
              └── IRepository<Student> (implementat de InMemoryRepository)
                    └── ArrayList<Student> (datele efective)
```

---

## 2. Structura pachete

```
com.catalog/
  Main.java                          -- 3 linii, punct de intrare

  app/
    CatalogApp.java                  -- orchestrator, demonstreaza toate actiunile

  model/                             -- entitatile domeniului
    BaseEntity.java
    Person.java
    Student.java
    Profesor.java
    Materie.java
    Nota.java
    Curs.java
    Inscriere.java
    enums/
      GradDidactic.java
      StatusInscriere.java
      TipMaterie.java

  repository/                        -- acces la date
    IRepository.java
    InMemoryRepository.java
    IUnitOfWork.java
    InMemoryUnitOfWork.java

  service/                           -- logica de business
    BaseService.java                 -- implementare generica CRUD
    StudentService.java
    ProfesorService.java
    MaterieService.java
    NotaService.java
    CursService.java
    InscriereService.java
    interfaces/                      -- contracte expuse catre exterior
      IReadable.java
      IInsertable.java
      IUpdatable.java
      IDeletable.java
      IStudentService.java
      IProfesorService.java
      IMaterieService.java
      INotaService.java
      ICursService.java
      IInscriereService.java

  exception/
    CatalogException.java
    EntityNotFoundException.java
    DuplicateEntityException.java
    ValidationException.java
    BusinessRuleException.java

  util/
    IdGenerator.java
```

---

## 3. Layere si responsabilitati

### Layer 1 — Model (`model/`)
Contine exclusiv date si validare la nivel de camp.
- **Nu apeleaza servicii, nu apeleaza repo-uri.**
- Validarea se face in setter: `setVarsta()` arunca `ValidationException` daca e in afara intervalului.
- `BaseEntity` genereaza ID-ul automat prin `IdGenerator`.

### Layer 2 — Repository (`repository/`)
Acces la date, zero logica de business.
- `IRepository<TEntity>` — contract CRUD generic.
- `InMemoryRepository<TEntity>` — implementare cu `ArrayList`. In Etapa II devine `JdbcRepository<TEntity>`.
- `IUnitOfWork` — punct unic de acces la toate repo-urile + `commit()`/`rollback()`.
- `InMemoryUnitOfWork` — creeaza un `InMemoryRepository` per tip de entitate.

### Layer 3 — Service (`service/`)
Logica de business. Stie despre repo-uri prin `IUnitOfWork`, nu stie nimic despre cum sunt implementate.
- `BaseService<TEntity>` — implementare generica CRUD (getById, getAll, insert, update, delete), elimina duplicarea.
- Serviciile concrete extind `BaseService` si adauga metode specifice domeniului.
- **Serviciile primesc `IUnitOfWork`**, nu `IRepository` individual — pot accesa orice repo au nevoie.

### Layer 4 — App (`app/`)
Orchestrare. `CatalogApp` leaga totul impreuna.
- Creeaza serviciile (injecteaza `IUnitOfWork`).
- `run()` demonstreaza toate cele 12 actiuni.
- **Nu contine logica de business** — delega totul la servicii.

---

## 4. Ierarhii de mostenire

### Entitati
```
BaseEntity  (abstract — are id, equals, hashCode)
  ├── Person  (abstract — are nume, varsta)
  │     ├── Student
  │     └── Profesor
  ├── Materie
  ├── Nota
  ├── Curs
  └── Inscriere
```

### Exceptii
```
RuntimeException
  └── CatalogException
        ├── EntityNotFoundException   -- getById/update pe id inexistent
        ├── DuplicateEntityException  -- insert cu id deja existent
        ├── ValidationException       -- camp invalid (varsta, nota)
        └── BusinessRuleException     -- regula de business incalcata (curs plin, deja inscris)
```

### Interfete servicii (Interface Segregation)
```
IReadable<TEntity>    -- getById, getAll
IInsertable<TEntity>  -- insert
IUpdatable<TEntity>   -- update
IDeletable<TEntity>   -- delete

IStudentService    extends IReadable + IInsertable + IUpdatable + IDeletable
IProfesorService   extends IReadable + IInsertable + IUpdatable + IDeletable
IMaterieService    extends IReadable + IInsertable + IUpdatable + IDeletable
ICursService       extends IReadable + IInsertable + IUpdatable + IDeletable
INotaService       extends IReadable + IInsertable              (fara update/delete — nota e imutabila)
IInscriereService  extends IReadable + IInsertable + IDeletable (fara update — folosesti inscrie/retrage)
```

---

## 5. Patterns folosite

### Repository Pattern
Abstractizeaza accesul la date. Serviciile nu stiu daca datele vin din memorie, CSV, sau PostgreSQL.
```
IRepository<TEntity>
  └── InMemoryRepository<TEntity>   (Etapa I)
  └── JdbcRepository<TEntity>       (Etapa II — de adaugat)
```

### Unit of Work Pattern
Un singur obiect care tine toate repo-urile si coordoneaza tranzactiile.
- In Etapa I: `commit()`/`rollback()` sunt no-op.
- In Etapa II: `commit()` = `connection.commit()`, `rollback()` = `connection.rollback()`.
- **Motivul principal**: serviciile care au nevoie de date cross-entity nu primesc N repo-uri — primesc un singur `IUnitOfWork`.

### Interface Segregation Principle (ISP)
In loc de o interfata monolitica `IService<T>` cu toate operatiile, avem interfete granulare.
Rezultat: `INotaService` nu expune `update()`/`delete()` — callerul nu poate apela operatii care nu au sens pentru `Nota`.

### Template Method (implicit prin BaseService)
`BaseService<TEntity>` defineste implementarea default a CRUD.
Subclasele o mostenesc si adauga doar ce e specific. `MaterieService` are 3 linii — constructorul si atat.

---

## 6. Decizii de design

### De ce relatii prin ID-uri (int) si nu referinte la obiecte?
`Nota` are `studentId` si `cursId`, nu `Student student` si `Curs curs`.
- **Motiv**: pregatire pentru Etapa II (JDBC). In baza de date relatiile sunt foreign keys (int-uri).
- Daca am tine referinte la obiecte, serializarea/deserializarea JDBC ar fi complicata si am risca circular references.

### De ce Student nu are campul `medie`?
Media se calculeaza dinamic din note prin `NotaService.calculeazaMedie(studentId)`.
- **Motiv**: sursa unica de adevar. Daca `medie` ar fi camp pe `Student`, ar putea fi out of sync cu notele reale. Denormalizare.
- Consecinta: sortarea dupa medie foloseste un `Comparator` extern in `StudentService`, nu `Comparable` pe `Student`.

### De ce serviciile primesc `IUnitOfWork` si nu `IRepository` individual?
`InscriereService` trebuie sa stie cati studenti sunt inscrisi la un curs ca sa verifice `maxStudenti`.
Are nevoie de `inscriereRepo` SI `cursRepo`. Daca am injecta repo-uri individual, constructorul ar creste cu fiecare dependenta noua.
Cu `IUnitOfWork`, serviciul ia ce are nevoie: `unitOfWork.getCursRepository()`.

### De ce `IdGenerator` tine contor per clasa si nu global?
```java
IdGenerator.nextId(this.getClass())
```
In baza de date, fiecare tabela are propriul auto-increment. `Student` cu `id=1` si `Profesor` cu `id=1` sunt entitati diferite, nu conflict.
Un contor global ar face `Student(id=1)`, `Profesor(id=2)`, `Materie(id=3)` — artificial si incompatibil cu modelul relational.

### De ce `equals`/`hashCode` in `BaseEntity` foloseste `getClass()`?
```java
if (obj == null || getClass() != obj.getClass()) return false;
```
Fara acest check, `Student(id=1).equals(Profesor(id=1))` ar returna `true` — acelasi id, dar entitati complet diferite.
`hashCode` foloseste `Objects.hash(getClass(), id)` — hash diferit pentru tipuri diferite cu acelasi id, evita coliziuni artificiale in `HashSet`/`HashMap`.

### De ce exceptii unchecked (RuntimeException)?
Exceptiile custom extind `CatalogException extends RuntimeException`, nu `Exception`.
- **Exceptii checked** (`throws Exception`) obliga caller-ul sa le prinda sau sa le declare — zgomot in fiecare semnatura de metoda.
- **Exceptii unchecked** lasa caller-ul sa decidă daca le prinde. Serviciile prind ce au nevoie, restul se propaga.
- Setters (`setVarsta`, `setValoare`) arunca `ValidationException` direct — caller-ul (serviciul) decide cum raspunde.

### De ce `INotaService` nu expune `update()`/`delete()`?
O nota pusa nu se modifica si nu se sterge prin fluxul normal al aplicatiei.
Prin ISP, daca `INotaService` nu declara aceste metode, callerul care lucreaza cu `INotaService` nu le poate apela — constrangere la nivel de compilare (atata timp cat folosesti tipul interfata, nu tipul concret).

---

## 7. Etapa II — ce se adauga

Schimbarea e **minima si izolata**. Serviciile, entitatile, orchestratorul — neschimbate.

```
repository/
  JdbcRepository.java        -- implements IRepository<TEntity> cu PreparedStatement
  JdbcUnitOfWork.java        -- implements IUnitOfWork, tine java.sql.Connection

util/
  DatabaseConnection.java    -- singleton, ofera Connection JDBC
  AuditService.java          -- singleton, scrie CSV: actiune_name, timestamp
```

**Singura modificare in cod existent** — o linie in `Main.java`:
```java
// Etapa I:
IUnitOfWork unitOfWork = new InMemoryUnitOfWork();

// Etapa II:
IUnitOfWork unitOfWork = new JdbcUnitOfWork("jdbc:postgresql://localhost/catalog", "user", "pass");
```

**Audit** — in fiecare metoda de serviciu se adauga un apel:
```java
AuditService.getInstance().log("adaugaStudent");
// scrie in CSV: adaugaStudent, 2026-04-15T10:30:00
```

---

## 8. Q&A — intrebari ipotetice

### De ce nu `IService<T>` monolitic in loc de `IReadable + IInsertable + ...`?
Un `IService<T>` cu toate 5 operatii obliga orice serviciu sa le implementeze pe toate, chiar daca unele nu au sens.
`INotaService` n-ar trebui sa aiba `update()` — o nota nu se editeaza. ISP (Interface Segregation Principle) zice: nu obliga clientii sa depinda de metode pe care nu le folosesc.

### Ce ma opreste sa apelez `delete()` pe un serviciu care nu implementeaza `IDeletable`?
Daca tii referinta ca interfata (`INotaService notaService`), compilatorul te opreste — metoda nu exista in interfata.
Daca tii referinta ca tip concret (`NotaService notaService`), `delete()` din `BaseService` e accesibila. Solutia completa ar fi constructor package-private pe servicii + `ServiceFactory` in acelasi pachet, care returneaza doar interfete. Atunci `new NotaService(uow)` din afara pachetului `service` nu compileaza.

### De ce `BaseService` si nu duplicam CRUD in fiecare serviciu?
`getById`, `getAll`, `insert`, `update`, `delete` ar fi identice in toate 6 serviciile — 30 de metode copiate. Orice modificare (ex: adaugare logging) ar trebui facuta in 6 locuri. `BaseService` centralizeaza implementarea; subclasele adauga doar ce e unic.

### Poate `BaseService` sa aiba metode `protected` ca sa previna accesul din exterior?
Da, dar cu un cost: fiecare serviciu care implementeaza o interfata (`IDeletable`) ar trebui sa override explicit ca `public`:
```java
@Override
public void delete(Student e) { super.delete(e); }
```
Asta readuce partial boilerplate-ul pe care voiam sa-l eliminam. Trade-off intre protectie stricta si cod concis.

### De ce `HashSet<Materie>` in `Profesor` si nu `List<Materie>`?
Un profesor nu poate preda aceeasi materie de doua ori — `HashSet` garanteaza unicitatea fara cod suplimentar. Functioneaza corect pentru ca `Materie` mosteneste `equals`/`hashCode` din `BaseEntity` (bazat pe `id`).

### De ce `TreeSet<Student>` in `getStudentiOrdonatiDupaMedie()` si nu `sort()` pe lista?
`TreeSet` mentine ordinea la fiecare `add()` — e o colectie sortata structural. `sort()` pe lista sorteaza o data la momentul apelului. Cerinta de la proiect e sa demonstrezi o colectie sortata (`TreeSet`, `TreeMap`). In plus, `TreeSet` previne duplicate automat (prin `Comparator`).

### De ce `Student` nu implementeaza `Comparable`?
`Comparable` ar defini o ordine naturala fixa (ex: alfabetic dupa nume). Dar vrem sa sortam dupa medie, care e calculata dinamic din note — nu e un camp pe `Student`. Un `Comparator` extern in `StudentService` are acces la `notaRepo` si poate calcula media la momentul comparatiei.

### Cum functioneaza `InscriereService.retrage()` daca `IInscriereService` nu expune `update()`?
`retrage()` apeleaza `repository.update(inscriere)` intern din `BaseService` — metoda `protected` e accesibila din subclasa. Din exterior, callerul vede doar `retrage(studentId, cursId)` — nu stie (si nu trebuie sa stie) ca intern se face un update. Encapsulare corecta.

### De ce relatiile dintre entitati nu sunt bidirectionale?
`Nota` are `studentId` si `cursId`, dar `Student` nu are `List<Nota> note`.
- Relatii bidirectionale in memorie creeaza riscul de circular references si complica serializarea.
- In modelul relational, navigarea e prin query (SELECT * FROM note WHERE studentId = ?), nu prin referinte.
- `NotaService.getNoteStudent(studentId)` face exact asta — filtreaza repo-ul de note.

### Cum ar arata un meniu interactiv in loc de secventa automata?
`CatalogApp.run()` ar fi inlocuit cu un loop `Scanner`:
```java
Scanner scanner = new Scanner(System.in);
while (true) {
    System.out.println("1. Adauga student  2. Adauga nota  ...");
    int optiune = scanner.nextInt();
    switch (optiune) {
        case 1: adaugaStudent(scanner); break;
        ...
    }
}
```
Serviciile raman identice — doar stratul de prezentare se schimba.

### De ce nu am folosit `Optional<TEntity>` in loc de `null` la `getById`?
La nivelul acestui proiect, `Optional` ar adauga complexitate fara beneficiu clar — ar forta `Optional.get()` sau `.orElse()` in fiecare caller. In Etapa II cu JDBC, `getById` returneaza `null` natural cand `ResultSet` e gol. Daca proiectul ar creste spre productie, `Optional` ar fi alegerea corecta.
