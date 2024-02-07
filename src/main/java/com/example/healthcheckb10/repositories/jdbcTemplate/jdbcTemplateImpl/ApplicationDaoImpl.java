package com.example.healthcheckb10.repositories.jdbcTemplate.jdbcTemplateImpl;

import com.example.healthcheckb10.dto.application.ApplicationResponse;
import com.example.healthcheckb10.dto.application.SearchApplicationResponse;
import com.example.healthcheckb10.repositories.jdbcTemplate.ApplicationDao;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApplicationDaoImpl implements ApplicationDao {
    private final JdbcTemplate jdbcTemplate;

    private ApplicationResponse rowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return new ApplicationResponse(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("phoneNumber"),
                resultSet.getBoolean("processed"),
                resultSet.getDate("createdAt")
        );
    }

    @Override
    public Optional<ApplicationResponse> getById(Long id) {
        String sql = """
                SELECT
                id AS id,
                first_name AS name,
                phone_number AS phoneNumber,
                processed AS processed,
                creating_application_date AS createdAt
                FROM applications a WHERE a.id = ?
                """;
        return jdbcTemplate.query(sql, this::rowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public List<ApplicationResponse> getAllApplications() {
        String sql = """
                SELECT
                id AS id,
                first_name AS name,
                phone_number AS phoneNumber,
                processed AS processed,
                creating_application_date AS createdAt
                FROM applications
                ORDER BY id DESC
                """;
        return jdbcTemplate.query(sql, this::rowMapper);
    }

    @Override
    public List<SearchApplicationResponse> globalSearch(String word) {
        word = "%" + word + "%";
        String sql = """
        SELECT a.id AS id,
               a.first_name AS name,
               TO_CHAR(a.creating_application_date, 'YYYY-MM-DD') AS createdAt,
               a.phone_number AS phoneNumber,
               a.processed AS processed
        FROM applications a
        WHERE a.first_name ILIKE ?
           OR TO_CHAR(a.creating_application_date, 'YYYY-MM-DD') ILIKE ?
           OR a.phone_number ILIKE ?
           ORDER BY a.id
        """;
        return jdbcTemplate.query(sql,
                (rs, rowNum) ->
                        new SearchApplicationResponse(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getString("createdAt"),
                                rs.getString("phoneNumber"),
                                rs.getBoolean("processed")),
                word, word, word);
    }
}