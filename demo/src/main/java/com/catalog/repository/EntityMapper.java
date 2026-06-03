package com.catalog.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.catalog.model.BaseEntity;

// Singura parte care difera de la o entitate la alta in accesul JDBC.
// JdbcRepository foloseste acest contract ca sa stie:
//   - in ce tabela scrie/citeste            -> tableName()
//   - ce coloane (in afara de id) are        -> columns()
//   - cum pune campurile obiectului in SQL   -> bindColumns()
//   - cum construieste obiectul dintr-un rand -> fromResultSet()
//
// Conventie importanta: in INSERT si UPDATE, coloanele non-id se leaga pe pozitiile 1..N
// (in ordinea din columns()), iar id-ul e mereu pus de JdbcRepository pe pozitia N+1.
public interface EntityMapper<TEntity extends BaseEntity> {

    String tableName();

    // Coloanele non-id, in ordinea in care le leaga bindColumns().
    String[] columns();

    // Leaga campurile entitatii (fara id) pe pozitiile 1..N ale PreparedStatement-ului.
    void bindColumns(PreparedStatement ps, TEntity entity) throws SQLException;

    // Construieste o entitate dintr-un rand al ResultSet-ului curent.
    TEntity fromResultSet(ResultSet rs) throws SQLException;
}
