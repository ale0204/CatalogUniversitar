package com.catalog.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.catalog.exception.CatalogException;
import com.catalog.exception.EntityNotFoundException;
import com.catalog.model.BaseEntity;

// Implementare GENERICA a IRepository<T> peste JDBC.
// Tot SQL-ul repetitiv (SELECT/INSERT/UPDATE/DELETE) sta o singura data aici.
// Ce difera de la o entitate la alta (tabela, coloane, mapare) vine prin EntityMapper<T>.
//
// Aceeasi clasa serveste toate cele 6 entitati => "serviciu generic de citire/scriere".
public class JdbcRepository<TEntity extends BaseEntity> implements IRepository<TEntity> {
    private final Connection connection;
    private final EntityMapper<TEntity> mapper;

    public JdbcRepository(Connection connection, EntityMapper<TEntity> mapper) {
        this.connection = connection;
        this.mapper = mapper;
    }

    @Override
    public TEntity getById(int id) {
        String sql = "SELECT * FROM " + mapper.tableName() + " WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.fromResultSet(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new CatalogException("Eroare la getById in " + mapper.tableName() + ": " + e.getMessage());
        }
    }

    @Override
    public List<TEntity> getAll() {
        String sql = "SELECT * FROM " + mapper.tableName() + " ORDER BY id";
        List<TEntity> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapper.fromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new CatalogException("Eroare la getAll in " + mapper.tableName() + ": " + e.getMessage());
        }
        return result;
    }

    @Override
    public void insert(TEntity entity) {
        String[] cols = mapper.columns();
        String sql = "INSERT INTO " + mapper.tableName()
                + " (" + String.join(", ", cols) + ", id) VALUES ("
                + placeholders(cols.length + 1) + ")";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            mapper.bindColumns(ps, entity);
            ps.setInt(cols.length + 1, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CatalogException("Eroare la insert in " + mapper.tableName() + ": " + e.getMessage());
        }
    }

    @Override
    public void update(TEntity entity) {
        String[] cols = mapper.columns();
        // UPDATE tabela SET col1=?, col2=?, ... WHERE id=?
        // Coloanele non-id pe 1..N, id pe N+1 (in clauza WHERE).
        StringBuilder set = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) set.append(", ");
            set.append(cols[i]).append(" = ?");
        }
        String sql = "UPDATE " + mapper.tableName() + " SET " + set + " WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            mapper.bindColumns(ps, entity);
            ps.setInt(cols.length + 1, entity.getId());
            int randuriAfectate = ps.executeUpdate();
            if (randuriAfectate == 0) {
                throw new EntityNotFoundException(mapper.tableName(), entity.getId());
            }
        } catch (SQLException e) {
            throw new CatalogException("Eroare la update in " + mapper.tableName() + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(TEntity entity) {
        String sql = "DELETE FROM " + mapper.tableName() + " WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CatalogException("Eroare la delete in " + mapper.tableName() + ": " + e.getMessage());
        }
    }

    private String placeholders(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) sb.append(", ");
            sb.append("?");
        }
        return sb.toString();
    }
}
