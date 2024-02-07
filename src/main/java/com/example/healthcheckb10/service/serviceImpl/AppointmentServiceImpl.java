package com.example.healthcheckb10.service.serviceImpl;

import com.example.healthcheckb10.dto.appointment.AppointmentResponse;
import com.example.healthcheckb10.dto.appointment.AppointmentResponseForGetById;
import com.example.healthcheckb10.dto.appointment.OnlineAppointmentResponse;
import com.example.healthcheckb10.dto.appointment.SearchResponse;
import com.example.healthcheckb10.dto.appointment.request.OnlineAppointmentRequest;
import com.example.healthcheckb10.dto.appointment.response.DoctorWithFreeDatesAndTimes;
import com.example.healthcheckb10.dto.appointment.response.DoctorWithFreeTimsheet;
import com.example.healthcheckb10.dto.appointment.response.DoctorsResponseByDepartment;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.entities.*;
import com.example.healthcheckb10.enums.Day;
import com.example.healthcheckb10.enums.Facility;
import com.example.healthcheckb10.enums.Status;
import com.example.healthcheckb10.exceptions.AlreadyExistsException;
import com.example.healthcheckb10.exceptions.BadCredentialException;
import com.example.healthcheckb10.exceptions.BadRequestException;
import com.example.healthcheckb10.exceptions.NotFoundException;
import com.example.healthcheckb10.repositories.*;
import com.example.healthcheckb10.repositories.jdbcTemplate.AppointmentDao;
import com.example.healthcheckb10.repositories.jdbcTemplate.DoctorDao;
import com.example.healthcheckb10.service.AppointmentService;
import com.example.healthcheckb10.service.EmailSenderService;
import jakarta.mail.MessagingException;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.context.Context;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentDao appointmentDao;
    private final UserAccountRepository userAccountRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final TimesheetRepository timesheetRepository;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;
    private final DoctorDao doctorDao;

    @Override
    public AppointmentResponseForGetById getAppointmentById(Long appointmentId) {
        AppointmentResponseForGetById appointmentResponseForGetById;
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() ->
                new NotFoundException("Запись под идентификатором " + appointmentId + " не найдена!"));
        if (appointment.getUser().getId().equals(getAuthenticationUser().getId())) {
            appointmentResponseForGetById = appointmentDao.getAppointmentById(appointmentId).orElseThrow(() -> {
                log.error("Запись под идентификатором : " + appointmentId + " не найдена!");
                return new NotFoundException("Запись под идентификатором : " + appointmentId + " не найдена!");
            });
            appointmentResponseForGetById.setDepartmentName(appointment.getDepartment().getFacilityName().getRussianName());
            appointmentResponseForGetById.setStatus(appointment.getStatus().getRussianName());
        } else {
            throw new BadCredentialException("Вы не можете просмотреть записи других пациентов!");
        }
        return appointmentResponseForGetById;
    }

    @Override
    public List<AppointmentResponse> getAllAppointmentByUser() {
        List<AppointmentResponse> appointmentResponses = appointmentDao.getAllAppointmentByUserId(getAuthenticationUser().getId());
        if (appointmentResponses.isEmpty()) {
            log.info("У Вас нет записей");
            return Collections.emptyList();
        }
        for (AppointmentResponse a : appointmentResponses) {
            a.setStatus(Status.valueOf(a.getStatus()).getRussianName());
        }
        return appointmentResponses;
    }

    @Override
    public List<SearchResponse> globalSearch(String word) {
        return appointmentDao.globalSearch(word);
    }

    public User getAuthenticationUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Пациент с почтовым адресом :" + email + "не найден!"));
        return userAccount.getUser();
    }

    @Override
    public OnlineAppointmentResponse createOnlineAppointment(OnlineAppointmentRequest appointmentRequest) throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Пациент с почтовым адресом :" + email + "не найден!"));
        String byCryptCode = userAccountRepository.findCode(userAccount.getId());
        if (byCryptCode != null && BCrypt.checkpw(appointmentRequest.getSmsCode(), byCryptCode)) {
            Department department = departmentRepository.findDepartmentByFacilityName(Facility.valueOf(appointmentRequest.getDepartmentName()))
                    .orElseThrow(() -> {
                        log.error("Отделение под названием %s не найдено!"
                                .formatted(appointmentRequest.getDepartmentName()));
                        return new NotFoundException("Отделение под названием %s не найдено!"
                                .formatted(appointmentRequest.getDepartmentName()));
                    });
            Doctor doctor = doctorRepository.findDoctorByDepartmentAndId(department, appointmentRequest.getDoctorId())
                    .orElseThrow(() -> {
                        log.error("Специалист под идентификатором %s не работает в отделении - %s!"
                                .formatted(appointmentRequest.getDoctorId(), department.getFacilityName()));
                        return new NotFoundException("Специалист под идентификатором %s не работает в отделении - %s!"
                                .formatted(appointmentRequest.getDoctorId(), department.getFacilityName()));
                    });
            User user = userAccount.getUser();
            Boolean booked = timesheetRepository.booked(doctor.getSchedule().getId(),
                    appointmentRequest.getDateOfVisiting(), LocalTime.parse(appointmentRequest.getTimeOfVisiting()));
            log.error("booked:" + booked);
            Timesheet timesheet = timesheetRepository.findTimesheet(doctor.getId(), appointmentRequest.getDateOfVisiting(),
                    LocalTime.parse(appointmentRequest.getTimeOfVisiting()));
            if (booked != null && !timesheet.getIsAvailable()) {
                throw new AlreadyExistsException("Это время занято!");
            } else if (booked == null) {
                throw new NotFoundException("Этот специалист не  работает в этот день или в это время ! Рабочие даты:c"
                        + doctor.getSchedule().getStartDateOfWork() + " до " + doctor.getSchedule().getEndDateOfWork());
            }
            Appointment appointment = new Appointment();
            appointment.setDepartment(department);
            appointment.setDoctor(doctor);
            appointment.setStatus(Status.CONFIRMED);
            appointment.setProcessed(false);
            appointment.setDateOfVisiting(appointmentRequest.getDateOfVisiting());
            appointment.setTimeOfVisiting(LocalTime.parse(appointmentRequest.getTimeOfVisiting()));
            int spaceIndex = appointmentRequest.getUserFullName().indexOf(" ");
            String firstName = appointmentRequest.getUserFullName().substring(0, spaceIndex);
            String lastName = appointmentRequest.getUserFullName().substring(spaceIndex + 1);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(appointmentRequest.getUserPhoneNumber());
            appointment.setUser(user);
            doctor.getAppointments().add(appointment);
            user.getAppointments().add(appointment);
            String date = appointment.getDateOfVisiting().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("ru")) + ", "
                    + appointment.getDateOfVisiting().getDayOfMonth() + " - "
                    + appointment.getDateOfVisiting().getMonth().getDisplayName(TextStyle.FULL, new Locale("ru"));
            String subject = "HealthCheck : Оповещение о записи";
            String time = generateGreeting("Asia/Bishkek");
            String emailAdmin="healthcheck05@gmail.com";
            String zoneId = "Asia/Bishkek";
            LocalDateTime timeNow = LocalDateTime.now(ZoneId.of(zoneId));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMMM EEEE", new Locale("ru", "RU"));
            String zonedDateTime = timeNow.format(dateTimeFormatter);
            Context context = new Context();
            context.setVariable("title", String.format(time + " %s ", appointment.getUser().getFirstName()));
            context.setVariable("department", department.getFacilityName().getRussianName());
            context.setVariable("doctor", doctor.getLastName() + " " + doctor.getFirstName());
            context.setVariable("date", date);
            context.setVariable("timeFrom", appointment.getTimeOfVisiting());
            context.setVariable("timeTo", timesheet.getEndTimeOfConsultation());
            context.setVariable("status", appointment.getStatus().name());
            context.setVariable("now", zonedDateTime);
            context.setVariable("emailAdmin",emailAdmin);
            context.setVariable("patient", appointment.getUser().getFirstName() + " " + appointment.getUser().getLastName());
            String phoneNumber = userAccountRepository.findByEmail("admin@gmail.com").orElseThrow(null).getUser().getPhoneNumber();
            context.setVariable("phoneNumber", phoneNumber);
            context.setVariable("imageDoctor", doctor.getImage());
            emailSenderService.sendEmail(userAccount.getEmail(), subject, "email-template", context);
            String startTimeAndEndTime = timesheet.getStartTimeOfConsultation().toString() + "-" + timesheet.getEndTimeOfConsultation();
            String doctorFullName = doctor.getFirstName() + " " + doctor.getLastName();
            DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
            LocalDate dateOfConsultation = timesheet.getDateOfConsultation();
            Day day = Day.valueOf(dayOfWeek.name());
            timesheet.setIsAvailable(false);
            timesheetRepository.save(timesheet);
            log.info("Timesheet успешно сохранен");
            Appointment saveAppointment = appointmentRepository.save(appointment);
            return OnlineAppointmentResponse.builder()
                    .day(day)
                    .date(dateOfConsultation)
                    .startTimeAndEndTime(startTimeAndEndTime)
                    .doctorFullName(doctorFullName)
                    .doctorImage(doctor.getImage())
                    .departmentName(department.getFacilityName().getRussianName())
                    .appointmentId(saveAppointment.getId())
                    .build();
        } else {
            throw new BadCredentialException("Произошла ошибка при сверке кода !");
        }
    }

    @Transactional
    @Override
    public SimpleResponse receiveCode(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        UserAccount userAccount = userAccountRepository.findByEmail(userEmail).orElseThrow(() ->
                new NotFoundException("Пациент с почтовым адресом :" + userEmail + "не найден!"));
        String generateNumber = generateNumber();
        if (email.equals(userAccount.getEmail())) {
            String greeting = generateGreeting("Asia/Bishkek");
            String subject = "HealthCheck :Оповещение о коде";
            Context context = new Context();
            context.setVariable("titleReceiveCode", String.format(greeting + " %s", userAccount.getUser().getFirstName()));
            context.setVariable("code", generateNumber);
            String encodeNumber = passwordEncoder.encode(generateNumber);
            userAccountRepository.addSmsCode(encodeNumber, userAccount.getId());
            log.info("Смс код на датабазу успешно сохранено");
            emailSenderService.sendEmail(userAccount.getEmail(), subject, "email-receive-template", context);
            log.info("Ваш код  успешно отправлено на почту");
            return SimpleResponse.builder()
                    .status(OK)
                    .message(generateNumber)
                    .build();
        } else {
            throw new NotFoundException("Пациент с почтовым адресом :" + email + " не найден!");
        }
    }

    private String generateNumber() {
        Random r = new Random();
        int min = 1000;
        int max = 9999;
        int randomNumber = r.nextInt(max - min + 1) + min;
        return String.format("%04d", randomNumber);
    }

    private String generateGreeting(String timeZone) {
        ZoneId userZone = ZoneId.of(timeZone);
        ZonedDateTime userTime = ZonedDateTime.now(userZone);
        String greeting;
        int hour = userTime.getHour();
        if (hour >= 6 && hour < 12) {
            greeting = "Доброе утро";
        } else if (hour >= 12 && hour < 18) {
            greeting = "Добрый день";
        } else if (hour >= 18 && hour < 24) {
            greeting = "Добрый вечер";
        } else {
            greeting = "Доброй ночи";
        }
        return greeting;
    }

    @Override
    public SimpleResponse canceled(Long appointmentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Пациент с почтовым адресом :" + email + "не найден!"));
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> {
            return new NotFoundException("Запись с идентификатором:" + appointmentId + " не найденa !");
        });
        if (appointment.getUser().getId().equals(userAccount.getUser().getId())) {
            Timesheet timesheet = timesheetRepository.findTimesheet(appointment.getDoctor().getId(),
                    appointment.getDateOfVisiting(), appointment.getTimeOfVisiting());
            timesheet.setIsAvailable(true);
            timesheetRepository.save(timesheet);
            appointment.setStatus(Status.CANCELED);
            appointmentRepository.save(appointment);
            log.info("Онлайн запись успешно удалена !");
            return SimpleResponse.builder()
                    .status(OK)
                    .message("Запись успешно отмененa!")
                    .build();
        } else {
            return SimpleResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Запрос не выполнен !")
                    .build();
        }
    }

    @Override
    public List<DoctorsResponseByDepartment> getDoctorsByDepartmentId(Long departmentId) {
        log.info("Метод getDoctorsByDepartmentId успешно началось");
        return doctorRepository.findDoctorsByDepartmentId(departmentId);
    }

    @Override
    public List<DoctorWithFreeTimsheet> getDoctorWithFreeTimesheets(Long doctorId) {
        log.info("Метод getDoctorWithFreeTimesheets успешно началось");
        ZoneId zoneId = ZoneId.of("Asia/Bishkek");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        LocalDate dateNow = zonedDateTime.toLocalDate();
        List<DoctorWithFreeTimsheet> doctorWithFreeTimesheets = doctorDao.findDoctorWithFreeTimesheets(doctorId, dateNow);
        if (!doctorWithFreeTimesheets.isEmpty()) {
            return doctorDao.findDoctorWithFreeTimesheets(doctorId, dateNow);
        } else {
            throw new NotFoundException("К сожалению, свободных времен для записи не найдено. Выберите другого специалиста");
        }
    }

    @Override
    public List<SearchResponse> getAllOnlineAppointments() {
        ZoneId zoneId = ZoneId.of("Asia/Bishkek");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        LocalDate dateNow = zonedDateTime.toLocalDate();
        return appointmentDao.getAllOnlineAppointments(dateNow);
    }

    @Override
    public SimpleResponse deleteAppointmentById(List<Long> appointmentIds) {
        List<Long> longs = new ArrayList<>();
        for (Long id : appointmentIds) {
            Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> {
                return new NotFoundException("Запись с идентификатором:" + id + " не найденa !");
            });
            if (appointment.getStatus().name().equals("COMPLETED")) {
                appointmentDao.deleteAppointmentById(appointment.getId());
                longs.add(appointment.getId());
            } else {
                throw new BadRequestException("Нельзя удалять еще не обработанную онлайн запись");
            }
        }
        return SimpleResponse.builder()
                .status(OK)
                .message("Онлайн запись с идентификатором:" + longs + " успешно удалена .")
                .build();
    }

    @Override
    public SimpleResponse updateStatusAppointmentById(Long appointmentId, Status status) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> {
            log.error("Онлайн запись с идентификатором:" + appointmentId + " не найденa !");
            return new NotFoundException("Онлайн запись с идентификатором:" + appointmentId + " не найденa !");
        });
        appointment.setStatus(status);
        appointmentRepository.save(appointment);
        if (appointment.getStatus().name().equals("CONFIRMED")) {
            log.info("Онлайн запись с идентификатором %s успешно подвержден !".formatted(appointmentId));
            return SimpleResponse.builder()
                    .status(OK)
                    .message("Онлайн запись с идентификатором %s успешно подвержден !".formatted(appointment.getId()))
                    .build();
        } else {
            log.info("Онлайн запись с идентификатором %s успешно завершен !".formatted(appointmentId));
            return SimpleResponse.builder()
                    .status(OK)
                    .message("Онлайн запись с идентификатором %s успешно завершен !".formatted(appointment.getId()))
                    .build();
        }
    }

    @Override
    public List<DoctorWithFreeDatesAndTimes> getDoctorWithFreeDatesAndTimes(Long doctorId) {
        log.info("Метод getDoctorWithFreeDatesAndTimes успешно началось");
        ZoneId zoneId = ZoneId.of("Asia/Bishkek");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        LocalDate dateNow = zonedDateTime.toLocalDate();
        List<DoctorWithFreeDatesAndTimes> doctorWithFreeDatesAndTimes = doctorDao.getDoctorWithFreeDatesAndTimes(doctorId, dateNow);
        if (!doctorWithFreeDatesAndTimes.isEmpty()) {
            return doctorWithFreeDatesAndTimes;
        } else {
            throw new NotFoundException("К сожалению, свободных времен для онлайн  записи не найдено. Выберите другого специалиста");
        }
    }
}