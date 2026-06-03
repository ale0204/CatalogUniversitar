package com.catalog.repository.mappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.catalog.model.Curs;
import com.catalog.repository.EntityMapper;

public class CursMapper implements EntityMapper<Curs> {

    @Override
    public String tableName() {
        return "curs";
    }

    @Override
    public String[] columns() {
        return new String[] { "materie_id", "profesor_id", "an_universitar", "semestru", "max_studenti" };
    }

    @Override
    public void bindColumns(PreparedStatement ps, Curs c) throws SQLException {
        ps.setInt(1, c.getMaterieId());
        ps.setInt(2, c.getProfesorId());
        ps.setString(3, c.getAnUniversitar());
        ps.setInt(4, c.getSemestru());
        ps.setInt(5, c.getMaxStudenti());
    }

    @Override
    public Curs fromResultSet(ResultSet rs) throws SQLException {
        Curs c = new Curs(
                rs.getInt("materie_id"),
                rs.getInt("profesor_id"),
                rs.getString("an_universitar"),
                rs.getInt("semestru"),
                rs.getInt("max_studenti"));
        c.setId(rs.getInt("id"));
        return c;
    }
}
