package com.catalog.repository.mappers;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.catalog.model.Inscriere;
import com.catalog.model.enums.StatusInscriere;
import com.catalog.repository.EntityMapper;

public class InscriereMapper implements EntityMapper<Inscriere> {

    @Override
    public String tableName() {
        return "inscriere";
    }

    @Override
    public String[] columns() {
        return new String[] { "student_id", "curs_id", "status", "data_inscriere" };
    }

    @Override
    public void bindColumns(PreparedStatement ps, Inscriere i) throws SQLException {
        ps.setInt(1, i.getStudentId());
        ps.setInt(2, i.getCursId());
        ps.setString(3, i.getStatus().name());
        ps.setDate(4, Date.valueOf(i.getDataInscriere()));
    }

    @Override
    public Inscriere fromResultSet(ResultSet rs) throws SQLException {
        Inscriere i = new Inscriere(rs.getInt("student_id"), rs.getInt("curs_id"));
        i.setStatus(StatusInscriere.valueOf(rs.getString("status")));
        i.setDataInscriere(rs.getDate("data_inscriere").toLocalDate());
        i.setId(rs.getInt("id"));
        return i;
    }
}
