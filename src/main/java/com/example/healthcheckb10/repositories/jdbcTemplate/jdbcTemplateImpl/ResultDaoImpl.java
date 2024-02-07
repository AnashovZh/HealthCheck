package com.example.healthcheckb10.repositories.jdbcTemplate.jdbcTemplateImpl;

import com.example.healthcheckb10.dto.result.AdminGetResultResponse;
import com.example.healthcheckb10.repositories.jdbcTemplate.ResultDao;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ResultDaoImpl implements ResultDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<AdminGetResultResponse> getResultForAdmin(Long userId) {
        String sql = """
                SELECT
                r.id AS id,
                u.first_name AS firstName,
                u.last_name AS lastName,
                ua.email AS email,
                u.phone_number AS phoneNumber,
                d.facility_name AS departmentName,
                r.date_of_uploading_result AS dateOfUploadingResult,
                TO_CHAR(r.time_of_uploading_result, 'HH24:MI') AS timeOfUploadingResult,
                r.result_number AS resultNumber,
                r.pdg_file_cheque AS pdgFileCheque
                FROM users u
                JOIN user_accounts ua ON ua.user_id = u.id
                JOIN results r ON r.user_id = u.id
                JOIN departments d ON d.id = r.department_id
                WHERE u.id=?
                """;
        return jdbcTemplate.query(
                        sql,
                        (resultSet, rowNum) ->
                                new AdminGetResultResponse(
                                        resultSet.getLong("id"),
                                        resultSet.getString("firstName"),
                                        resultSet.getString("lastName"),
                                        resultSet.getString("email"),
                                        resultSet.getString("phoneNumber"),
                                        resultSet.getString("departmentName"),
                                        resultSet.getDate("dateOfUploadingResult").toLocalDate(),
                                        resultSet.getString("timeOfUploadingResult"),
                                        resultSet.getString("resultNumber"),
                                        resultSet.getString("pdgFileCheque"))
                        , userId);
    }
}