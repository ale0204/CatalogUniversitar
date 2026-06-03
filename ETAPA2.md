# Etapa II - Persistenta JDBC (PostgreSQL) + Audit CSV

> Document didactic. Explica **pas cu pas** ce implementam in Etapa II si **de ce**.
> Fiecare pas are: *Ce facem*, *De ce*, *Fisiere afectate* si nota teoretica.

---

## 0. Recapitulare

In Etapa I am construit totul pe **interfete**:

```
Main
  └── IUnitOfWork            <-- interfata
        └── IRepository<T>   <-- interfata
              └── ArrayList  (InMemoryRepository)
```

Serviciile primesc `IUnitOfWork`, nu `InMemoryUnitOfWork`. Nu stiu si nu le pasa
de unde vin datele. Asta inseamna ca daca scriem o noua implementare a acelorasi
interfete care vorbeste cu PostgreSQL, putem **inlocui implementarea fara sa
atingem nimic deasupra**.

> **Principiul**: Dependency Inversion. Codul de nivel inalt (servicii) depinde de
> abstractii (interfete), nu de detalii (clasa concreta de stocare).

Concret, Etapa II = adaugam o a doua implementare:

```
IUnitOfWork
  ├── InMemoryUnitOfWork   (Etapa I - ramane, util pt teste)
  └── JdbcUnitOfWork       (Etapa II - NOU, vorbeste cu PostgreSQL)

IRepository<T>
  ├── InMemoryRepository<T>  (Etapa I)
  └── JdbcRepository<T>      (Etapa II - NOU)
```

Si schimbam **o linie** in `Main`.

---

## Ce cere enuntul (proiect.md)

1. **Persistenta** cu baza de date relationala + **JDBC**.
   - Servicii **CRUD** pentru cel putin **4 clase** (noi facem toate 6 entitatile).
   - Servicii **singleton generice** pentru scriere/citire in DB.
2. **Serviciu de audit**: scrie intr-un **CSV** de fiecare data cand se executa una
   dintre actiunile din Etapa I. Structura: `nume_actiune, timestamp`.

---

## Decizii confirmate pentru Etapa II

| Decizie | Alegere | De ce |
|---|---|---|
| Baza de date | **PostgreSQL 17** (deja instalat local, port 5432) | Cerinta: baza relationala. E deja pe masina. |
| Build / driver | **VS Code** + driver JDBC ca JAR in `lib/` | Nu folosim Maven. Driverul e un singur `.jar`. |
| Credentiale | user `postgres`, parola in `database.properties` | Config separat de cod, usor de schimbat. |
| Generare ID | **Pastram `IdGenerator`**, seedat din `MAX(id)` la pornire | Zero schimbari in entitati/servicii. Persistenta reala intre rulari, fara coliziuni de cheie primara. |
| Cate entitati persistam | **Toate 6** (Student, Profesor, Materie, Curs, Nota, Inscriere) | Enuntul cere min. 4; noi le facem pe toate cu acelasi cod generic. |
| Tranzactii | `autoCommit = true` | `CatalogApp` nu apeleaza `commit()`; ca sa ramana neschimbat, fiecare operatie se salveaza imediat. `commit()/rollback()` raman cablate la conexiunea reala pt cand ar fi nevoie. |

---

## Harta fisierelor NOI (nimic vechi nu se rescrie, cu 2 exceptii mici)

```
demo/
  lib/
    postgresql-42.7.x.jar          <-- NOU: driverul JDBC (descarcat)
  database.properties              <-- NOU: url + user + parola
  schema.sql                       <-- NOU: comenzile CREATE TABLE

  src/main/java/com/catalog/
    util/
      DatabaseConnection.java       <-- NOU: singleton, ofera Connection
      AuditService.java             <-- NOU: singleton, scrie CSV
      IdGenerator.java              <-- MODIFICAT: adaugam metoda seed()
    repository/
      RowMapper.java                <-- NOU: ResultSet -> entitate
      StatementBinder.java          <-- NOU: entitate -> PreparedStatement
      JdbcRepository.java           <-- NOU: IRepository<T> generic cu SQL
      JdbcUnitOfWork.java           <-- NOU: IUnitOfWork cu Connection
      mappers/
        StudentMapper.java          <-- NOU: maparea Student <-> rand SQL
        ProfesorMapper.java         <-- NOU
        MaterieMapper.java          <-- NOU
        CursMapper.java             <-- NOU
        NotaMapper.java             <-- NOU
        InscriereMapper.java        <-- NOU
    Main.java                       <-- MODIFICAT: o linie (InMemory -> Jdbc)
    service/ ...                    <-- MODIFICAT doar pt audit (apeluri log)
```

> **Singurele fisiere existente atinse**: `Main.java` (1 linie), `IdGenerator.java`
> (o metoda noua) si serviciile (un apel de audit per actiune). Entitatile,
> repository-urile in-memory, orchestratorul `run()` - neschimbate.

---

## Pasii de implementare

### Pas 0 - Pregatire mediu(facut)
- PostgreSQL 17 ruleaza pe `localhost:5432`.
- Am creat baza de date `catalog`.
- Urmeaza: descarcam driverul JDBC in `lib/` si il legam in classpath-ul VS Code.

### Pas 1 - Schema bazei de date (`schema.sql`)
**Ce facem**: scriem `CREATE TABLE` pentru cele 6 entitati si rulam scriptul.
**De ce**: JDBC scrie/citeste randuri; tabelele trebuie sa existe intai. Coloanele
oglindesc exact campurile entitatilor. Relatiile prin `*_id` (int) din Etapa I
devin coloane normale (optional foreign keys).

### Pas 2 - `DatabaseConnection` (singleton)
**Ce facem**: o clasa singleton care citeste `database.properties` si ofera un
`java.sql.Connection` unic.
**De ce**: enuntul cere "servicii singleton". O singura conexiune partajata =
o singura poarta catre DB. `getConnection()` returneaza mereu aceeasi instanta.

### Pas 3 - `JdbcRepository<T>` generic (+ `RowMapper`, `StatementBinder`)
**Ce facem**: implementarea generica a `IRepository<T>` cu `PreparedStatement`.
Partea care difera de la o entitate la alta (numele tabelei, coloanele, cum se
citeste un rand) o injectam prin doua interfete mici: `RowMapper<T>` (rand -> obiect)
si `StatementBinder<T>` (obiect -> parametri SQL).
**De ce**: Java generics nu stiu singure ce coloane are `Student`. In loc de
reflection (magie, greu de depanat), facem maparea **explicita** per entitate.
Codul SQL repetitiv (SELECT/INSERT/UPDATE/DELETE) sta o singura data in
`JdbcRepository`; doar maparea e per-entitate. = "serviciu generic de citire/scriere".

### Pas 4 - `JdbcUnitOfWork`
**Ce facem**: implementam `IUnitOfWork` peste JDBC. Creeaza cele 6 `JdbcRepository`,
tine `Connection`-ul, si la pornire **seedeaza `IdGenerator`** din `MAX(id)` al
fiecarei tabele. `commit()/rollback()` deleaga la `Connection`.
**De ce**: punct unic de acces la toate repo-urile JDBC, exact ca `InMemoryUnitOfWork`.
Seed-ul ID-urilor evita coliziuni de cheie primara la re-rulari.

### Pas 5 - `IdGenerator.seed()` + `Main` (o linie)
**Ce facem**: adaugam `IdGenerator.seed(clasa, valoare)`; schimbam in `Main`
`new InMemoryUnitOfWork()` cu `new JdbcUnitOfWork()`.
**De ce**: momentul "platii": demonstram ca arhitectura permite swap-ul cu o linie.

### Pas 6 - `AuditService` (singleton, CSV)
**Ce facem**: singleton care scrie in `audit.csv` linii `nume_actiune,timestamp`.
**De ce**: cerinta 2 din Etapa II. Singleton ca sa scrie toti in acelasi fisier.

### Pas 7 - Apeluri de audit in actiuni
**Ce facem**: in fiecare metoda care corespunde unei actiuni din Etapa I, adaugam
`AuditService.getInstance().log("numeActiune")`.
**De ce**: "de fiecare data cand se executa o actiune". Logam la sursa (in servicii),
nu in orchestrator, ca sa se prinda oricine apeleaza actiunea.

### Pas 8 - Rulare si verificare
**Ce facem**: rulam aplicatia, verificam ca datele ajung in PostgreSQL
(`SELECT * FROM student`) si ca `audit.csv` se populeaza.
**De ce**: dovada ca persistenta si auditul functioneaza end-to-end.

---

## Nota teoretica: ce e JDBC (pe scurt)

JDBC (Java Database Connectivity) = API-ul standard Java pt baze relationale.
Piesele pe care le folosim:
- **`Connection`** - sesiunea catre DB. O deschizi o data, o refolosesti.
- **`PreparedStatement`** - un SQL parametrizat (`INSERT ... VALUES (?, ?)`).
  Parametrii `?` se completeaza cu `setInt/setString/...`. Previne SQL injection
  si compileaza interogarea o data.
- **`ResultSet`** - rezultatul unui `SELECT`, parcurs rand cu rand cu `next()`.
- **Driver** - JAR-ul specific PostgreSQL care implementeaza JDBC pt acest DB.

Progresul se actualizeaza in acest document pe masura ce avansam.

---

## Cum compilezi si rulezi (din linia de comanda, fara Maven)

Driverul JDBC e in `demo/lib/postgresql-42.7.4.jar`. Din PowerShell, din folderul `demo/`:

```powershell
# Compilare (toate sursele -> folderul build/)
$src = Get-ChildItem .\src\main\java -Recurse -Filter *.java | % { $_.FullName }
javac -d build -cp "lib\postgresql-42.7.4.jar" $src

# Rulare (clasa Main; din demo/ ca sa gaseasca database.properties si sa scrie audit.csv)
java -cp "build;lib\postgresql-42.7.4.jar" com.catalog.Main
```

In **VS Code**: extensia Java vede dependinta `postgresql` din `pom.xml` si o descarca singura
(foloseste Maven-ul ei intern). Apesi Run pe `Main.java` ca de obicei.

> **Important**: rularea trebuie sa porneasca din folderul `demo/`, pentru ca acolo sunt
> `database.properties` (citit la pornire) si `audit.csv` (scris in timpul rularii).

---

## Verificare end-to-end (dovada ca merge)

**1. Datele ajung in PostgreSQL** (`SELECT count(*)` dupa o rulare):

| Tabela | Randuri |
|---|---|
| student | 4 |
| profesor | 2 |
| materie | 3 |
| curs | 2 |
| nota | 5 |
| inscriere | 5 |

Statusul `RETRAS` se vede in `inscriere` (actiunea 11 a persistat corect), iar testul
"curs plin" arunca `BusinessRuleException` ca in Etapa I - logica de business e neschimbata.

**2. `audit.csv`** contine toate cele 12 tipuri de actiuni, ex:
```
nume_actiune,timestamp
adaugaStudent,2026-05-29T15:56:35
...
retragereStudentDinCurs,2026-05-29T15:56:35
afiseazaNoteleStudentului,2026-05-29T15:56:35
```

---

## Doua observatii didactice (nu sunt bug-uri)

### 1. ID-urile au "goluri" (1, 4, 9, 16, ...)
ID-urile sunt generate de client (`IdGenerator`), iar `BaseEntity` ii cere un id in
constructor. Cand `getAll()` citeste randuri din DB, mapper-ul face `new Student(...)`
ca sa reconstruiasca obiectul - iar acel constructor **consuma un numar din contor**
inainte ca `setId()` sa-l suprascrie cu id-ul real din DB. Deci fiecare citire
"arde" cateva numere de contor. Rezultatul: id-uri noi cu goluri.

Nu e o problema de corectitudine: noile id-uri sunt mereu **deasupra** a tot ce exista
in DB (pentru ca seedam contorul din `MAX(id)` la pornire), deci nu apar coliziuni de
cheie primara. Bazele de date reale au si ele goluri in secvente (dupa rollback-uri etc.).

### 2. Datele se acumuleaza intre rulari
Pentru ca acum avem persistenta reala, fiecare rulare **adauga** date noi (demo-ul creeaza
studenti/cursuri noi de fiecare data). Asta NU e bug - exact asta inseamna persistenta.

Ca sa controlam asta, `Main` intreaba la pornire:
```
Golesc baza de date inainte de rulare? (y/n):
```
- **y** => `JdbcUnitOfWork.curataToateTabelele()` face `TRUNCATE ... RESTART IDENTITY CASCADE`
  si re-seedeaza `IdGenerator` => porneste de la zero, id-uri de la 1.
- **n** => pastreaza datele existente (utile ca sa DEMONSTREZI persistenta intre rulari).

Acest prompt **nu e cerut de enunt** - e doar o conveniență de demo. Golirea ruleaza doar pe
varianta JDBC (verificata cu `instanceof`), deci `Main` ramane scris pe interfata `IUnitOfWork`.

Alternativ, manual din `psql`:
```sql
TRUNCATE inscriere, nota, curs, materie, profesor, student RESTART IDENTITY CASCADE;
```

### 3. Atentie: nu tine `audit.csv` deschis in Excel cand rulezi
Excel blocheaza fisierul exclusiv. Daca `audit.csv` e deschis in Excel (sau alt program care-l
blocheaza) in timp ce rulezi, scrierea in CSV esueaza. Inchide-l inainte de rulare.

---

## Maparea cerintelor enuntului → cod

| Cerinta Etapa II | Unde e implementata |
|---|---|
| Persistenta cu baza relationala + JDBC | PostgreSQL + `JdbcRepository`/`JdbcUnitOfWork`/`DatabaseConnection` |
| CRUD pentru min. 4 clase | Toate 6 entitatile (Student, Profesor, Materie, Curs, Nota, Inscriere) via `JdbcRepository<T>` |
| Servicii **singleton** generice de citire/scriere | `DatabaseConnection` (singleton conexiune) + `JdbcRepository<T>` (generic, refolosit de toate entitatile) |
| Audit CSV (`nume_actiune, timestamp`) | `AuditService` (singleton) + apeluri in cele 12 actiuni |
