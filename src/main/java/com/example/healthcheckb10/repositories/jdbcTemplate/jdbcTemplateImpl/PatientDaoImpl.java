package com.example.healthcheckb10.repositories.jdbcTemplate.jdbcTemplateImpl;

import com.example.healthcheckb10.dto.user.response.PatientGetByIdResponse;
import com.example.healthcheckb10.dto.user.response.PatientResponse;
import com.example.healthcheckb10.repositories.jdbcTemplate.PatientDao;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatientDaoImpl implements PatientDao {
    private final JdbcTemplate jdbcTemplate;

    private PatientResponse rowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return new PatientResponse(
                resultSet.getLong("id"),
                resultSet.getString("fullName"),
                resultSet.getString("phoneNumber"),
                resultSet.getString("email"),
                resultSet.getString("date")
        );
    }

    @Override
    public Optional<PatientGetByIdResponse> getPatientById(Long id) {
        String sql = """
                SELECT
                u.id AS id,
                first_name AS firstName,
                last_name AS lastName,
                phone_number AS phoneNumber,
                email AS email
                FROM user_accounts ua JOIN users u on ua.user_id = u.id WHERE ua.role!='ADMIN' AND u.id=?
                 """;
        return jdbcTemplate.query(sql,
                (resultSet, rowNum) ->
                        new PatientGetByIdResponse(
                                resultSet.getLong("id"),
                                resultSet.getString("firstName"),
                                resultSet.getString("lastName"),
                                resultSet.getString("phoneNumber"),
                                resultSet.getString("email")),id)
                .stream()
                .findFirst();
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        String sql = """
                SELECT
                    id,
                    fullName,
                    phoneNumber,
                    email,
                    date
                FROM
                    (
                        SELECT
                            u.id AS id,
                            CONCAT(first_name, ' ', last_name) AS fullName,
                            phone_number AS phoneNumber,
                            email AS email,
                            TO_CHAR(r.date_of_uploading_result, 'YYYY-MM-DD') AS date,
                            ROW_NUMBER() OVER (PARTITION BY u.id ORDER BY r.date_of_uploading_result DESC) AS result_rank
                        FROM
                            users u
                                JOIN user_accounts ua ON ua.user_id = u.id
                                LEFT JOIN results r ON u.id = r.user_id
                        WHERE
                                ua.role != 'ADMIN'
                    ) ranked_results
                WHERE
                        result_rank = 1
                ORDER BY
                    CASE WHEN date IS NULL THEN 1 ELSE 0 END, COALESCE(date, '9999-12-31') DESC, id DESC
                """;
        return jdbcTemplate.query(sql, this::rowMapper);
    }

    @Override
    public List<PatientResponse> getAllPatientsBySearch(String word) {
        word = "%" + word + "%";
        String sql = """
                SELECT
                    id,
                    fullName,
                    phoneNumber,
                    email,
                    date
                FROM
                    (
                        SELECT
                            u.id AS id,
                            CONCAT(first_name, ' ', last_name) AS fullName,
                            phone_number AS phoneNumber,
                            email AS email,
                            TO_CHAR(r.date_of_uploading_result, 'YYYY-MM-DD') AS date,
                            ROW_NUMBER() OVER (PARTITION BY u.id ORDER BY r.date_of_uploading_result DESC) AS result_rank
                        FROM
                            users u
                                JOIN user_accounts ua ON ua.user_id = u.id
                                LEFT JOIN results r ON u.id = r.user_id
                        WHERE
                                ua.role <> 'ADMIN' AND (first_name ILIKE '%s' OR last_name ILIKE '%s')
                    ) ranked_results
                WHERE
                        result_rank = 1
                ORDER BY
                    CASE WHEN date IS NULL THEN 1 ELSE 0 END, COALESCE(date, '9999-12-31') DESC, id DESC
                """;
        sql = String.format(sql, word, word);
        return jdbcTemplate.query(sql,
                (resultSet, rowNum) ->
                        new PatientResponse(
                                resultSet.getLong("id"),
                                resultSet.getString("fullName"),
                                resultSet.getString("phoneNumber"),
                                resultSet.getString("email"),
                                resultSet.getString("date")
                        ));
    }
}