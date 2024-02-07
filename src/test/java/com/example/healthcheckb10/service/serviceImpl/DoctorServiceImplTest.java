package com.example.healthcheckb10.service.serviceImpl;

import com.example.healthcheckb10.dto.doctor.DoctorRequest;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.entities.Department;
import com.example.healthcheckb10.enums.Facility;
import com.example.healthcheckb10.exceptions.AlreadyExistsException;
import com.example.healthcheckb10.repositories.DepartmentRepository;
import com.example.healthcheckb10.repositories.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import com.example.healthcheckb10.exceptions.NotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {
    @InjectMocks
    private DoctorServiceImpl doctorService;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private final DoctorRequest doctorRequest = DoctorRequest.builder()
            .firstName("Sadyr")
            .lastName("Japarov")
            .image("path/to/image11.jpg")
            .position("president")
            .description("aita beret kaita beret")
            .build();
    private final Long departmentId = 1L;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void createDoctor_Successful() {
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(createMockDepartment()));
        SimpleResponse result = doctorService.createDoctor(doctorRequest, departmentId);
        assertEquals(HttpStatus.OK, result.status());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void createDoctor_DuplicateDoctor() {
        DoctorRequest doctorRequest = DoctorRequest.builder()
                .firstName("Алексей")
                .lastName("Смирнов")
                .image("https://ivanovo.smclinic.ru/upload/iblock/08f/q9vemqs92s6po1xiurri86gwk8ruhlx7/terapevt-iv.jpg")
                .position("president")
                .description("aita beret kaita beret")
                .build();
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(createMockDepartment()));
        when(doctorRepository.existsByImageAndFirstNameAndLastName(
                doctorRequest.image(),
                doctorRequest.firstName(),
                doctorRequest.lastName())).thenReturn(true);
        Exception exception = assertThrows(AlreadyExistsException.class,
                () -> doctorService.createDoctor(doctorRequest, departmentId));
        assertEquals("Доктор с такими данными уже существует!", exception.getMessage());
        verify(doctorRepository, never()).save(any());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void createDoctor_DepartmentNotFound() {
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());
        try {
            doctorService.createDoctor(doctorRequest, departmentId);
        } catch (NotFoundException e) {
            assertEquals("Отделение под идентификатором 1 не найдено!", e.getMessage());
        }
    }

    private Department createMockDepartment() {
        Department department = new Department();
        department.setId(1L);
        department.setFacilityName(Facility.ALLERGOLOGY);
        return department;
    }
}