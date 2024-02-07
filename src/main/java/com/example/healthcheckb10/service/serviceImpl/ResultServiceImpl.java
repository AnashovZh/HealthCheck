package com.example.healthcheckb10.service.serviceImpl;
import com.example.healthcheckb10.dto.result.AdminGetResultResponse;
import com.example.healthcheckb10.dto.result.GetResultResponse;
import com.example.healthcheckb10.dto.result.ResultRequest;
import com.example.healthcheckb10.dto.result.ResultResponse;
import com.example.healthcheckb10.entities.Department;
import com.example.healthcheckb10.entities.Result;
import com.example.healthcheckb10.entities.User;
import com.example.healthcheckb10.enums.Facility;
import com.example.healthcheckb10.exceptions.BadCredentialException;
import com.example.healthcheckb10.exceptions.NotFoundException;
import com.example.healthcheckb10.repositories.DepartmentRepository;
import com.example.healthcheckb10.repositories.ResultRepository;
import com.example.healthcheckb10.repositories.UserRepository;
import com.example.healthcheckb10.repositories.jdbcTemplate.ResultDao;
import com.example.healthcheckb10.service.EmailSenderService;
import com.example.healthcheckb10.service.ResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultServiceImpl implements ResultService {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ResultRepository resultRepository;
    private final AppointmentServiceImpl appointmentService;
    private final EmailSenderService emailSenderService;
    private final ResultDao resultDao;

    @Override
    public ResultResponse  addResult(ResultRequest resultRequest) {
        User user = userRepository.findById(resultRequest.patientId()).orElseThrow(() ->
                new NotFoundException("Пациент с ID: %s не найден!".formatted(resultRequest.patientId())));
        Department department = departmentRepository.findById(resultRequest.departmentId()).orElseThrow(() ->
                new NotFoundException("Отделение с ID: %s  не найдено!".formatted(resultRequest.departmentId())));
        String orderNumber = generateOrder();
        ZoneId zoneId = ZoneId.of("Asia/Bishkek");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        LocalTime timeNow = zonedDateTime.toLocalTime();
        Result result = Result.builder()
                .department(department)
                .dateOfUploadingResult(resultRequest.dueDate())
                .timeOfUploadingResult(timeNow)
                .resultNumber(orderNumber)
                .pdgFileCheque(resultRequest.pdgFileCheque())
                .user(user)
                .build();
        user.addResult(result);
        resultRepository.save(result);
        log.info("Результат с полным именем пациента: %s успешно добавлен!".formatted(user.getFirstName() + " " + user.getLastName()));
        Context context = new Context();
        context.setVariable("patientName", user.getFirstName() + " " + user.getLastName());
        context.setVariable("departmentName", department.getFacilityName().getRussianName());
        context.setVariable("generateNumber", orderNumber);
        emailSenderService.sendEmail(user.getUserAccount().getEmail(), "HealthCheck : Оповещение о результате", "result_template", context);
        log.info("Сообщение отправлено пользователю с email : %s".formatted(user.getUserAccount().getEmail()));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = timeNow.format(dateTimeFormatter);
        return ResultResponse.builder()
                .id(result.getId())
                .departmentName(String.valueOf(department.getFacilityName().getRussianName()))
                .userid(user.getId())
                .dateOfUploadingResult(resultRequest.dueDate())
                .timeOfUploadingResult(formattedTime)
                .orderNumber(orderNumber)
                .pdgFileCheque(resultRequest.pdgFileCheque())
                .build();
    }
    
    @Override
    public GetResultResponse getResult(String resultNumber) {
        Result result = resultRepository.findByResultNumber(resultNumber).orElseThrow(() ->
                new NotFoundException(("Результат с номером результата: %s не найден." +
                        " Пожалуйста, введите правильный номер результата").formatted(resultNumber)));
        User authUser = appointmentService.getAuthenticationUser();
        if (result.getUser().equals(authUser)) {
            return new GetResultResponse(result.getId(),result.getPdgFileCheque());
        } else {
            throw new BadCredentialException("Вы не можете видеть результаты других");
        }
    }

    @Override
    public List<AdminGetResultResponse> getResultForAdmin(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пациент с ID: %s не найден!".formatted(userId)));
        if(resultDao.getResultForAdmin(user.getId()).isEmpty()){
            return Collections.emptyList();
        }else{
            List<AdminGetResultResponse> results = resultDao.getResultForAdmin(user.getId());
            for(AdminGetResultResponse result : results){
                result.setDepartmentName(Facility.valueOf(result.getDepartmentName()).getRussianName());
            }
            return results;
        }
    }

    private String generateOrder() {
        UUID orderId = UUID.randomUUID();
        String orderNumber = orderId.toString().replaceAll("-", "");
        if (orderNumber.length() > 18) {
            orderNumber = orderNumber.substring(0, 18);
        }
        return orderNumber;
    }
}