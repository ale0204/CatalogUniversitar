-- Etapa II: schema bazei de date "catalog" (PostgreSQL).
-- Fiecare tabela oglindeste o entitate din model/. Coloanele = campurile clasei.
-- id ramane INT PRIMARY KEY (nu SERIAL) pentru ca ID-urile sunt generate de client (IdGenerator).
-- "IF NOT EXISTS" => scriptul se poate rula de mai multe ori fara eroare.

CREATE TABLE IF NOT EXISTS student (
    id     INTEGER PRIMARY KEY,
    nume   VARCHAR(200) NOT NULL,
    varsta INTEGER      NOT NULL
);

CREATE TABLE IF NOT EXISTS profesor (
    id            INTEGER PRIMARY KEY,
    nume          VARCHAR(200) NOT NULL,
    varsta        INTEGER      NOT NULL,
    grad_didactic VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS materie (
    id           INTEGER PRIMARY KEY,
    nume_materie VARCHAR(200) NOT NULL,
    tip_materie  VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS curs (
    id             INTEGER PRIMARY KEY,
    materie_id     INTEGER     NOT NULL REFERENCES materie(id),
    profesor_id    INTEGER     NOT NULL REFERENCES profesor(id),
    an_universitar VARCHAR(20) NOT NULL,
    semestru       INTEGER     NOT NULL,
    max_studenti   INTEGER     NOT NULL
);

CREATE TABLE IF NOT EXISTS nota (
    id         INTEGER PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES student(id),
    curs_id    INTEGER NOT NULL REFERENCES curs(id),
    valoare    REAL    NOT NULL,
    data       DATE    NOT NULL
);

CREATE TABLE IF NOT EXISTS inscriere (
    id             INTEGER PRIMARY KEY,
    student_id     INTEGER     NOT NULL REFERENCES student(id),
    curs_id        INTEGER     NOT NULL REFERENCES curs(id),
    status         VARCHAR(20) NOT NULL,
    data_inscriere DATE        NOT NULL
);
