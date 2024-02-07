package com.example.healthcheckb10.service.serviceImpl;

import com.example.healthcheckb10.dto.doctor.DoctorRequest;
import com.example.healthcheckb10.dto.doctor.DoctorResponse;
import com.example.healthcheckb10.dto.doctor.SearchDoctorResponse;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.entities.Department;
import com.example.healthcheckb10.entities.Doctor;
import com.example.healthcheckb10.enums.Facility;
import com.example.healthcheckb10.entities.Schedule;
import com.example.healthcheckb10.exceptions.AlreadyExistsException;
import com.example.healthcheckb10.exceptions.BadCredentialException;
import com.example.healthcheckb10.exceptions.NotFoundException;
import com.example.healthcheckb10.repositories.DepartmentRepository;
import com.example.healthcheckb10.repositories.DoctorRepository;
import com.example.healthcheckb10.repositories.jdbcTemplate.DoctorDao;
import com.example.healthcheckb10.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorDao doctorDao;

    @Override
    public SimpleResponse createDoctor(DoctorRequest doctorRequest, Long departmentId) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() ->
                new NotFoundException(
                        "Отделение под идентификатором %s не найдено!".formatted(departmentId))
        );

        Doctor doctor = new Doctor();
        doctor.setFirstName(doctorRequest.firstName());
        doctor.setLastName(doctorRequest.lastName());
        doctor.setImage(doctorRequest.image());
        doctor.setPosition(doctorRequest.position());
        doctor.setDescription(doctorRequest.description());
        doctor.setIsActive(true);
        doctor.setDepartment(department);
        if (doctorRepository.existsByImageAndFirstNameAndLastName(
                doctorRequest.image(),
                doctorRequest.firstName(),
                doctorRequest.lastName())) {
            log.error("Доктор с такими данными уже существует!");
            throw new AlreadyExistsException("Доктор с такими данными уже существует!");
        }
        doctorRepository.save(doctor);
        log.info("Доктор с идентификатором %s успешно сохранен!".formatted(doctor.getId()));
        return new SimpleResponse(
                HttpStatus.OK,
                "Доктор с идентификатором %s успешно сохранен!".formatted(doctor.getId()));
    }

    @Override
    public DoctorResponse getDoctorById(Long doctorId) {
        DoctorResponse doctorResponse = doctorDao.getDoctorById(doctorId).orElseThrow(() -> {
            log.error("Доктор с идентификатором %s не найден! ".formatted(doctorId));
            return new NotFoundException(
                    "Доктор с идентификатором %s не найден! ".formatted(doctorId));
        });
        doctorResponse.setDepartmentName(Facility.valueOf(doctorResponse.getDepartmentName()).getRussianName());
        return doctorResponse;
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        List<DoctorResponse> doctors = doctorDao.getAllDoctors();
        for (DoctorResponse response : doctors) {
            response.setDepartmentName(Facility.valueOf(response.getDepartmentName()).getRussianName());
        }
        if (doctors.isEmpty()) {
            log.error("Список докторов не найден!");
            return Collections.emptyList();
        }
        return doctors;
    }

    @Override
    public SimpleResponse updateDoctor(DoctorRequest doctorRequest, Long doctorId, Long departmentId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> {
                    log.error("Доктор с идентификатором %s не найден! ".formatted(doctorId));
                    return new NotFoundException(
                            "Доктор с идентификатором %s не найден! ".formatted(doctorId));
                });
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> {
                    log.error("Отделение с идентификатором %s не найдено! ".formatted(departmentId));
                    return new NotFoundException(
                            "Отделение с идентификатором %s не найдено! ".formatted(departmentId));
                });
        doctor.setFirstName(doctorRequest.firstName());
        doctor.setLastName(doctorRequest.lastName());
        doctor.setImage(doctorRequest.image());
        doctor.setPosition(doctorRequest.position());
        doctor.setDescription(doctorRequest.description());
        doctor.setDepartment(department);
        doctorRepository.save(doctor);
        log.info("Доктор с идентификатором %s успешно обновлен!".formatted(doctor.getId()));
        return new SimpleResponse(
                HttpStatus.OK,
                "Доктор с идентификатором %s успешно обновлен!".formatted(doctor.getId()));
    }

    @Override
    public SimpleResponse deleteDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            log.error("Доктор под идентификатором %s, которую вы хотите удалить не существует!".formatted(doctorId));
            return new SimpleResponse(
                    HttpStatus.NOT_FOUND,
                    "Доктор под идентификатором %s, которую вы хотите удалить не существует!".formatted(doctorId));
        } else {
            int result = doctorDao.deleteById(doctorId);
            if(result == 1){
                log.info("Доктор под идентификатором %s успешно удален!".formatted(doctorId));
                return new SimpleResponse(
                        HttpStatus.OK,
                        "Доктор под идентификатором %s успешно удален!".formatted(doctorId)
                );
            }
            else {
                throw new BadCredentialException("Вы не можете удалить активного доктора !!! ");
            }
        }
    }

    @Override
    public List<SearchDoctorResponse> globalSearch(String word) {
        List<SearchDoctorResponse> doctorResponses = doctorDao.globalSearch(word);
        for (SearchDoctorResponse response : doctorResponses) {
            response.setDepartmentName(Facility.valueOf(response.getDepartmentName()).getRussianName());
        }
        return doctorResponses;
    }

    @Override
    public SimpleResponse manageStatus(Long doctorId, Boolean isActive) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> {
                    log.error("Доктор с идентификатором %s не найден! ".formatted(doctorId));
                    return new NotFoundException(
                            "Доктор с идентификатором %s не найден! ".formatted(doctorId));
                });
        Schedule schedule = doctor.getSchedule();
        if (schedule!=null){
            throw new BadCredentialException("Вы не можете изменить статус этого доктора !!!");
        }
        doctor.setIsActive(isActive);
        doctorRepository.save(doctor);
        if(doctor.getIsActive()){
            log.info("Статус доктора под идентификатором %s успешно изменен на активный режим!".formatted(doctorId));
            return new SimpleResponse(
                    HttpStatus.OK,
                    "Статус доктора под идентификатором %s успешно изменен на активный режим!".formatted(doctorId)
            );
        }else {
            log.info("Статус доктора под идентификатором %s успешно изменен на неактивный режим!".formatted(doctorId));
            return new SimpleResponse(
                    HttpStatus.OK,
                    "Статус доктора под идентификатором %s успешно изменен на неактивный режим!".formatted(doctorId)
            );
        }
    }
}