package com.catalog.repository.mappers;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.catalog.model.Nota;
import com.catalog.repository.EntityMapper;

public class NotaMapper implements EntityMapper<Nota> {

    @Override
    public String tableName() {
        return "nota";
    }

    @Override
    public String[] columns() {
        return new String[] { "student_id", "curs_id", "valoare", "data" };
    }

    @Override
    public void bindColumns(PreparedStatement ps, Nota n) throws SQLException {
        ps.setInt(1, n.getStudentId());
        ps.setInt(2, n.getCursId());
        ps.setFloat(3, n.getValoare());
        // LocalDate -> java.sql.Date pentru coloana DATE.
        ps.setDate(4, Date.valueOf(n.getData()));
    }

    @Override
    public Nota fromResultSet(ResultSet rs) throws SQLException {
        Nota n = new Nota(rs.getInt("student_id"), rs.getInt("curs_id"), rs.getFloat("valoare"));
        n.setData(rs.getDate("data").toLocalDate());
        n.setId(rs.getInt("id"));
        return n;
    }
}
