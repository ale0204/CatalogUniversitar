package com.catalog.repository.mappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.catalog.model.Student;
import com.catalog.repository.EntityMapper;

public class StudentMapper implements EntityMapper<Student> {

    @Override
    public String tableName() {
        return "student";
    }

    @Override
    public String[] columns() {
        return new String[] { "nume", "varsta" };
    }

    @Override
    public void bindColumns(PreparedStatement ps, Student s) throws SQLException {
        ps.setString(1, s.getNume());
        ps.setInt(2, s.getVarsta());
    }

    @Override
    public Student fromResultSet(ResultSet rs) throws SQLException {
        Student s = new Student(rs.getString("nume"), rs.getInt("varsta"));
        s.setId(rs.getInt("id"));
        return s;
    }
}
