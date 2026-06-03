package com.catalog.repository.mappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.catalog.model.Materie;
import com.catalog.model.enums.TipMaterie;
import com.catalog.repository.EntityMapper;

public class MaterieMapper implements EntityMapper<Materie> {

    @Override
    public String tableName() {
        return "materie";
    }

    @Override
    public String[] columns() {
        return new String[] { "nume_materie", "tip_materie" };
    }

    @Override
    public void bindColumns(PreparedStatement ps, Materie m) throws SQLException {
        ps.setString(1, m.getNumeMaterie());
        ps.setString(2, m.getTipMaterie().name());
    }

    @Override
    public Materie fromResultSet(ResultSet rs) throws SQLException {
        Materie m = new Materie(
                rs.getString("nume_materie"),
                TipMaterie.valueOf(rs.getString("tip_materie")));
        m.setId(rs.getInt("id"));
        return m;
    }
}
