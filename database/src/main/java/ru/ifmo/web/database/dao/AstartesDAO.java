package ru.ifmo.web.database.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ifmo.web.database.entity.Astartes;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
public class AstartesDAO {
    private final DataSource dataSource;

    private final String TABLE_NAME = "astartes";
    private final String ID = "id";
    private final String NAME = "name";
    private final String TITLE = "title";
    private final String POSITION = "position";
    private final String PLANET = "planet";
    private final String BIRTHDATE = "birthdate";

    private final List<String> columnNames = Arrays.asList(ID, NAME, TITLE, POSITION, PLANET, BIRTHDATE);

    public List<Astartes> findAll() throws SQLException {
        log.info("Find all");
        try (Connection connection = dataSource.getConnection()) {
            java.sql.Statement statement = connection.createStatement();
            StringBuilder query = new StringBuilder();
            statement.execute(query.append("SELECT ").append(String.join(", ", columnNames)).append(" FROM ").append(TABLE_NAME).toString());
            List<Astartes> result = resultSetToList(statement.getResultSet());
            return result;
        }
    }

    public List<Astartes> findWithFilters(Long id, String name, String title, String position, String planet, Date birthdate) throws SQLException {
        log.debug("Find with filters: {} {} {} {} {} {}", id, name, title, position, planet, birthdate);
        if (Stream.of(id, name, title, position, planet, birthdate).allMatch(Objects::isNull)) {
            return findAll();
        }

        StringBuilder query = new StringBuilder();
        query.append("SELECT ").append(String.join(",", columnNames)).append(" FROM ").append(TABLE_NAME).append(" WHERE ");
        int i = 1;
        List<Statement> statements = new ArrayList<>();
        if (id != null) {
            query.append(ID).append("= ?");
            statements.add(new Statement(i, id, getSqlType(Long.class)));
            i++;
        }
        if (name != null) {
            if (!statements.isEmpty()) {
                query.append(" AND ");
            }
            query.append(NAME).append("= ?");
            statements.add(new Statement(i, name, getSqlType(String.class)));
            i++;
        }
        if (title != null) {
            if (!statements.isEmpty()) {
                query.append(" AND ");
            }
            query.append(TITLE).append("= ?");
            statements.add(new Statement(i, title, getSqlType(String.class)));
            i++;
        }
        if (position != null) {
            if (!statements.isEmpty()) {
                query.append(" AND ");
            }
            query.append(POSITION).append("= ?");
            statements.add(new Statement(i, position, getSqlType(String.class)));
            i++;
        }
        if (planet != null) {
            if (!statements.isEmpty()) {
                query.append(" AND ");
            }
            query.append(PLANET).append("= ?");
            statements.add(new Statement(i, planet, getSqlType(String.class)));
            i++;
        }
        if (birthdate != null) {
            if (!statements.isEmpty()) {
                query.append(" AND ");
            }
            query.append(BIRTHDATE).append("= ?");
            statements.add(new Statement(i, birthdate, getSqlType(Date.class)));
        }

        log.debug("Query string {}", query.toString());
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query.toString());
            fillPreparedStatement(ps, statements);
            ResultSet rs = ps.executeQuery();
            return resultSetToList(rs);
        }

    }

    public Long create(String name, String title, String position, String planet, Date birthdate) throws SQLException {
        log.debug("Create with params {} {} {} {} {}", name, title, position, planet, birthdate);
        try (Connection connection = dataSource.getConnection()) {
            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO ").append(TABLE_NAME).append("(").append(String.join(",", columnNames)).append(") VALUES(?,?,?,?,?,?)");
            connection.setAutoCommit(false);
            long newId;
            try (java.sql.Statement idStatement = connection.createStatement()) {
                idStatement.execute("SELECT nextval('astartes_id_seq') nextval");
                try (ResultSet rs = idStatement.getResultSet()) {
                    rs.next();
                    newId = rs.getLong("nextval");
                }

            }
            try (PreparedStatement stmnt = connection.prepareStatement(query.toString())) {
                stmnt.setLong(1, newId);
                stmnt.setString(2, name);
                stmnt.setString(3, title);
                stmnt.setString(4, position);
                stmnt.setString(5, planet);
                stmnt.setDate(6, new java.sql.Date(birthdate.getTime()));
                int count = stmnt.executeUpdate();
                if (count == 0) {
                    throw new RuntimeException("Could not execute query");
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
            return newId;
        }
    }

    public int delete(long id) throws SQLException {
        log.debug("Delete with id {}", id);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE id = ?")) {
                ps.setLong(1, id);
                return ps.executeUpdate();
            }
        }
    }

    public int update(long id, String name, String title, String position, String planet, Date birthdate) throws SQLException {
        log.debug("Update id {} and new values {} {} {} {} {}", id, name, title, position, planet, birthdate);
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(true);
            StringBuilder query = new StringBuilder("UPDATE " + TABLE_NAME + " SET ");
            int i = 1;
            List<Statement> statements = new ArrayList<>();
            if (name != null) {
                query.append(NAME).append("= ?");
                statements.add(new Statement(i, name, getSqlType(String.class)));
                i++;
            }
            if (title != null) {
                if (!statements.isEmpty()) {
                    query.append(", ");
                }
                query.append(TITLE).append("= ?");
                statements.add(new Statement(i, title, getSqlType(String.class)));
                i++;
            }
            if (position != null) {
                if (!statements.isEmpty()) {
                    query.append(", ");
                }
                query.append(POSITION).append("= ?");
                statements.add(new Statement(i, position, getSqlType(String.class)));
                i++;
            }
            if (planet != null) {
                if (!statements.isEmpty()) {
                    query.append(", ");
                }
                query.append(PLANET).append("= ?");
                statements.add(new Statement(i, planet, getSqlType(String.class)));
                i++;
            }
            if (birthdate != null) {
                if (!statements.isEmpty()) {
                    query.append(", ");
                }
                query.append(BIRTHDATE).append("= ?");
                statements.add(new Statement(i, birthdate, getSqlType(Date.class)));
                i++;
            }

            statements.add(new Statement(i, id, getSqlType(Long.class)));
            query.append(" WHERE id = ?");
            try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
                fillPreparedStatement(ps, statements);
                int updated = ps.executeUpdate();
                return updated;
            }
        }
    }

    private List<Astartes> resultSetToList(ResultSet rs) throws SQLException {
        List<Astartes> result = new ArrayList<>();
        while (rs.next()) {
            result.add(resultSetToEntity(rs));
        }
        return result;
    }

    private Astartes resultSetToEntity(ResultSet rs) throws SQLException {
        long id = rs.getLong(ID);
        String name = rs.getString(NAME);
        String title = rs.getString(TITLE);
        String position = rs.getString(POSITION);
        String planet = rs.getString(PLANET);
        Date birthdate = rs.getDate(BIRTHDATE);
        return new Astartes(id, name, title, position, planet, birthdate);
    }

    private void fillPreparedStatement(PreparedStatement ps, List<Statement> statements) throws SQLException {
        for (Statement statement : statements) {
            if (statement.getValue() == null) {
                ps.setNull(statement.number, statement.sqlType);
            } else {
                switch (statement.getSqlType()) {
                    case Types.BIGINT:
                        ps.setLong(statement.number, (Long) statement.getValue());
                        break;
                    case Types.VARCHAR:
                        ps.setString(statement.number, (String) statement.getValue());
                        break;
                    case Types.TIMESTAMP:
                        ps.setDate(statement.number, (java.sql.Date) statement.getValue());
                        break;
                    default:
                        throw new RuntimeException(statement.toString());
                }
            }
        }
    }

    private int getSqlType(Class<?> clazz) {
        if (clazz == Long.class) {
            return Types.BIGINT;
        } else if (clazz == String.class) {
            return Types.VARCHAR;
        } else if (clazz == Date.class) {
            return Types.TIMESTAMP;
        }
        throw new IllegalArgumentException(clazz.getName());
    }

    @Data
    @AllArgsConstructor
    private static class Statement {
        private int number;
        private Object value;
        private int sqlType;
    }

}
