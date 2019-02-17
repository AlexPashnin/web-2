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
