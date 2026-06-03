package com.catalog.repository.mappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.catalog.model.Profesor;
import com.catalog.model.enums.GradDidactic;
import com.catalog.repository.EntityMapper;

public class ProfesorMapper implements EntityMapper<Profesor> {

    @Override
    public String tableName() {
        return "profesor";
    }

    @Override
    public String[] columns() {
        return new String[] { "nume", "varsta", "grad_didactic" };
    }

    @Override
    public void bindColumns(PreparedStatement ps, Profesor p) throws SQLException {
        ps.setString(1, p.getNume());
        ps.setInt(2, p.getVarsta());
        // Enum-ul se salveaza ca text (name()), se citeste cu valueOf().
        ps.setString(3, p.getGradDidactic().name());
    }

    @Override
    public Profesor fromResultSet(ResultSet rs) throws SQLException {
        Profesor p = new Profesor(
                rs.getString("nume"),
                rs.getInt("varsta"),
                GradDidactic.valueOf(rs.getString("grad_didactic")));
        p.setId(rs.getInt("id"));
        return p;
    }
}
