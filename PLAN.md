# Plan: Implementare Proiect Catalog - Etapa I & II

## Context
Proiect POO Java - catalog universitar (student, materie, profesor, note, cursuri).
- **Etapa I**: 20-24 aprilie 2026. **Etapa II**: 1-5 iunie 2026.
- Stil: clean code C#-like (repository/UoW, interfete, mostenire), camelCase Java, nu ultra-modern.
- Scopul: implementare progresiva, structura pregatita pt Etapa II fara refactoring.
- Orchestrator cu secventa automata de demo (nu meniu interactiv). Posibil UI mai tarziu.

## Decizii confirmate
- Entitati noi: **Nota**, **Curs**, **Inscriere** + enum-uri **StatusInscriere**, **TipMaterie**
- Pachet radacina: **com.catalog** (inlocuieste com.example + com.enums)
- Metode: **camelCase** Java (getAll, insert, getById)
- Relatii prin **ID-uri (int)**, nu referinte la obiecte
- **IService<T>** generic pastrat
- **Student.medie** eliminat - se calculeaza din note via NotaService
- Exceptii **propagate** (nu inghitite in setter)
- Curs: materieId, profesorId, anUniversitar, semestru, maxStudenti
- Inscriere: studentId, cursId, status (ACTIV/RETRAS/FINALIZAT), dataInscriere
- Lista actiuni in **README.md** separat
- **Serviciile primesc IUnitOfWork** (nu IRepository individual) -- fiecare serviciu acceseaza ce repo-uri are nevoie
- **Student NU implementeaza Comparable** -- sortare prin Comparator extern in StudentService
- **JUnit 5** adaugat in pom.xml pentru teste de baza

---

## Structura pachete finala

```
com.catalog/
  Main.java
  app/
    CatalogApp.java                    -- orchestrator, primeste IUnitOfWork
  model/
    BaseEntity.java                    -- abstract, id auto-generat
    Person.java                        -- abstract, extends BaseEntity (nume, varsta)
    Student.java                       -- extends Person (fara medie!)
    Profesor.java                      -- extends Person (gradDidactic), implements Comparable
    Materie.java                       -- extends BaseEntity (numeMaterie, tipMaterie)
    Nota.java                          -- extends BaseEntity (studentId, cursId, valoare, data)
    Curs.java                          -- extends BaseEntity (materieId, profesorId, an, sem, max)
    Inscriere.java                     -- extends BaseEntity (studentId, cursId, status, data)
    enums/
      GradDidactic.java
      StatusInscriere.java             -- ACTIV, RETRAS, FINALIZAT
      TipMaterie.java                  -- OBLIGATORIE, OPTIONALA, FACULTATIVA
  repository/
    IRepository.java                   -- generic, returneaza List<T> nu ArrayList<T>
    InMemoryRepository.java            -- implementare cu ArrayList (redenumit din Repository)
    IUnitOfWork.java                   -- acces la toate repo-urile + commit/rollback
    InMemoryUnitOfWork.java            -- creeaza InMemoryRepository-uri, commit=no-op
  service/
    IService.java                      -- generic CRUD base, toate serviciile primesc IUnitOfWork
    IStudentService.java               -- extends IService<Student>, + getStudentiOrdonati()
    StudentService.java                -- primeste IUnitOfWork, acceseaza studentRepo + notaRepo pt medie
    IProfesorService.java
    ProfesorService.java
    IMaterieService.java
    MaterieService.java
    INotaService.java                  -- + calculeazaMedie(studentId), getNoteStudent()
    NotaService.java                   -- acceseaza notaRepo + studentRepo din UoW
    ICursService.java
    CursService.java
    IInscriereService.java             -- + inscrie(), retrage()
    InscriereService.java              -- acceseaza inscriereRepo + cursRepo din UoW (verifica maxStudenti)
  exception/
    CatalogException.java              -- extends RuntimeException (baza ierarhiei)
    EntityNotFoundException.java       -- extends CatalogException
    DuplicateEntityException.java      -- extends CatalogException
    ValidationException.java           -- extends CatalogException
    BusinessRuleException.java         -- extends CatalogException (curs plin, deja inscris)
  util/
    IdGenerator.java
```

## Tipuri de obiecte (11 total)
1. BaseEntity (abstract) | 2. Person (abstract) | 3. Student | 4. Profesor | 5. Materie
6. Nota | 7. Curs | 8. Inscriere | 9. GradDidactic (enum) | 10. StatusInscriere (enum) | 11. TipMaterie (enum)

## 12 Actiuni/Interogari
1. adaugaStudent - creeaza student nou
2. adaugaProfesor - creeaza profesor nou
3. adaugaMaterie - creeaza materie noua
4. creeazaCurs - creeaza curs (leaga materie + profesor, seteaza capacitate)
5. inscriereStudentLaCurs - inscrie student la curs (verifica maxStudenti)
6. adaugaNota - pune nota unui student la un curs
7. calculeazaMedieStudent - calculeaza media din note pentru un student
8. afiseazaStudentiOrdonatiDupaMedie - TreeSet sortat descrescator dupa medie calculata
9. afiseazaMateriiProfesor - listeaza materiile predate de un profesor
10. afiseazaStudentiInscrisiLaCurs - listeaza studentii inscrisi la un curs
11. retragereStudentDinCurs - schimba status inscriere in RETRAS
12. afiseazaNoteleStudentului - listeaza toate notele unui student

## Colectii demonstrate
- **ArrayList<T>** - in InMemoryRepository (storage principal)
- **HashSet<Materie>** - in Profesor.materiiPredate (colectie fara duplicate)
- **TreeSet<Student>** - in StudentService.getStudentiOrdonatiDupaMedie() (colectie **sortata**)
- **TreeMap<String, Float>** (optional) - medii pe materii, sortate alfabetic

---

## Pasi implementare Etapa I (progresivi, cu explicatii)

### Pas 1: Restructurare pachete
**Ce facem**: Mutam tot din com.example si com.enums in com.catalog cu structura noua.
**De ce**: Structura actuala are pachete inconsistente. com.catalog reflecta domeniul.
**Fisiere afectate**: Toate. Schimbam `package` si `import` statements.
- Redenumim `Repository` -> `InMemoryRepository`
- Mutam `StudentService` din `Services/Interfaces/` in `service/`
- Mutam `GradDidactic` in `com.catalog.model.enums`
- Verificam compilarea

### Pas 2: Fix cod existent + conventii
**Ce facem**: Corectam problemele din codul actual.
- `IRepository.getAll()` returneaza `List<T>` nu `ArrayList<T>`
- Metode din PascalCase in camelCase (GetAll -> getAll, Insert -> insert, etc.)
- Adaugam `equals()` si `hashCode()` pe BaseEntity (bazat pe id)
- `toString()` pe Student sa apeleze super.toString()
- Adaugam `toString()` pe Person, Profesor, Materie
- Scoatem campul `medie` din Student (se va calcula din note)
**De ce**: Conventii Java corecte, pregatire pentru colectii (equals/hashCode necesar pt Set/Map).

### Pas 3: Exceptii custom
**Ce facem**: Cream ierarhia de exceptii in pachetul `exception/`.
- `CatalogException extends RuntimeException` - baza
- `EntityNotFoundException` - cand getById nu gaseste
- `DuplicateEntityException` - insert cu id existent
- `ValidationException` - varsta/nota invalida
- `BusinessRuleException` - curs plin, student deja inscris
- Inlocuim `throws Exception` in Person.setVarsta cu `throws ValidationException`
- Propagam exceptia (nu mai inghitim cu try-catch in setter)
- Inlocuim `IllegalArgumentException` in repo Update cu EntityNotFoundException
**De ce**: Exceptii specifice = debugging mai usor, ierarhie = demonstreaza mostenire.

### Pas 4: Entitati noi
**Ce facem**: Cream Nota, Curs, Inscriere + enum-urile noi.
- `StatusInscriere`: ACTIV, RETRAS, FINALIZAT
- `TipMaterie`: OBLIGATORIE, OPTIONALA, FACULTATIVA
- Adaugam `TipMaterie tipMaterie` pe Materie
- `Nota`: studentId (int), cursId (int), valoare (float 1-10), data (LocalDate)
- `Curs`: materieId (int), profesorId (int), anUniversitar (String), semestru (int), maxStudenti (int)
- `Inscriere`: studentId (int), cursId (int), status (StatusInscriere), dataInscriere (LocalDate)
- `Student implements Comparable<Student>` - compara dupa medie calculata? Nu, compararea va fi prin Comparator extern in service.
- equals/hashCode si toString pe fiecare entitate noua
**De ce**: Ajungem la 11 tipuri de obiecte. Nota e entitatea centrala a unui catalog.

### Pas 5: Unit of Work
**Ce facem**: Cream IUnitOfWork si InMemoryUnitOfWork.
- `IUnitOfWork`: getStudentRepository(), getProfesorRepository(), getMaterieRepository(), getNotaRepository(), getCursRepository(), getInscriereRepository(), commit(), rollback()
- `InMemoryUnitOfWork`: instantiaza InMemoryRepository<T> pentru fiecare tip, commit/rollback = no-op
**De ce**: Punct unic de acces la toate repo-urile. In Etapa II, swap cu JdbcUnitOfWork fara sa schimbam serviciile.

### Pas 6: Servicii noi
**Ce facem**: Implementam toate serviciile.
- `IService<T>` pastrat ca interfata generica de baza (getById, getAll, insert, update, delete)
- **Fiecare serviciu primeste IUnitOfWork** (nu IRepository individual). Serviciul acceseaza ce repo-uri are nevoie direct din UoW.
  - Exemplu: `StudentService(IUnitOfWork unitOfWork)` -> intern foloseste `unitOfWork.getStudentRepository()`
  - `InscriereService` poate accesa si `unitOfWork.getCursRepository()` ca sa verifice maxStudenti
  - `NotaService` poate accesa si `unitOfWork.getStudentRepository()` pentru calcul medie
- Servicii specifice extind IService<T> si adauga metode domain:
  - `IStudentService`: getStudentiOrdonatiDupaMedie() -- Comparator extern, returneaza TreeSet
  - `INotaService`: calculeazaMedie(int studentId), getNoteStudent(int studentId)
  - `ICursService`: getStudentiInscrisi(int cursId)
  - `IInscriereService`: inscrie(int studentId, int cursId), retrage(int studentId, int cursId)
  - `IProfesorService`: getMateriiPredate(int profesorId)
- **Student NU implementeaza Comparable** -- sortare prin `Comparator<Student>` in StudentService bazat pe medie calculata
**De ce**: Fiecare serviciu are acces la intreaga "baza de date" prin UoW. Separation of concerns: logica de business in servicii, datele in repo-uri, coordonarea in CatalogApp.

### Pas 7: CatalogApp orchestrator
**Ce facem**: Cream CatalogApp care:
- Primeste IUnitOfWork in constructor
- Instantiaza toate serviciile
- Metoda `run()` demonstreaza toate cele 12 actiuni secvential cu System.out.println
- Fiecare actiune cu separator vizual si explicatie in consola
**De ce**: Main devine 3 linii. Orchestratorul coordoneaza totul.

### Pas 8: Main.java + README.md
**Ce facem**: 
- Main: `IUnitOfWork uow = new InMemoryUnitOfWork(); CatalogApp app = new CatalogApp(uow); app.run();`
- README.md: lista celor 12 actiuni + lista celor 11 tipuri de obiecte
**De ce**: Cerinta spune "sa se creeze o lista". README.md e locul natural.

### Pas 9: JUnit 5 - teste de baza
**Ce facem**: Adaugam JUnit 5 in pom.xml si cateva teste:
- Test repository CRUD (insert, getById, getAll, update, delete)
- Test exceptii (ValidationException la varsta invalida, EntityNotFoundException la getById inexistent)
- Test calcul medie student
- Test inscriere la curs plin (BusinessRuleException)
**De ce**: Teste automate valideaza ca logica functioneaza. Demonstreaza profesionalism chiar daca nu e cerut explicit.

### Pas 10: Verificare cerinte
- [ ] 8+ tipuri de obiecte (avem 11)
- [ ] 10+ actiuni (avem 12)
- [ ] 2+ colectii, minim una sortata (ArrayList, HashSet, TreeSet)
- [ ] Mostenire (BaseEntity->Person->Student/Profesor, BaseEntity->Materie/Nota/Curs/Inscriere, CatalogException->children)
- [ ] Cel putin o clasa serviciu (avem 6)
- [ ] Main apeleaza servicii prin orchestrator
- [ ] Compileaza fara erori
- [ ] Ruleaza si produce output corect

---

## Pregatire Etapa II (ce nu facem acum dar e pregatit)

In Etapa II adaugam DOAR:
1. **DatabaseConnection.java** in `util/` - singleton, JDBC connection
2. **JdbcRepository<T>** in `repository/` - implements IRepository<T> cu SQL
3. **JdbcUnitOfWork** in `repository/` - implements IUnitOfWork, manages Connection
4. **AuditService.java** in `util/` - singleton, scrie CSV (actiune, timestamp)
5. Schimbam O LINIE in Main: `new InMemoryUnitOfWork()` -> `new JdbcUnitOfWork(...)`
6. Adaugam apeluri audit in servicii

**Nu refactorizam nimic** - serviciile, entitatile, orchestratorul raman identice.

---

## Verificare end-to-end
1. `mvn compile` - trebuie sa compileze fara erori
2. `mvn exec:java -Dexec.mainClass="com.catalog.Main"` - ruleaza demo-ul
3. Output-ul trebuie sa arate toate cele 12 actiuni executate cu succes
4. Verificam ca TreeSet-ul ordoneaza corect studentii dupa medie
5. Verificam ca exceptiile custom sunt aruncate si prinse corect (ex: inscriere la curs plin)
