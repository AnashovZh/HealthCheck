package com.example.healthcheckb10.service.serviceImpl;

import com.example.healthcheckb10.dto.application.ApplicationRequest;
import com.example.healthcheckb10.dto.application.ApplicationResponse;
import com.example.healthcheckb10.dto.application.SearchApplicationResponse;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.entities.Application;
import com.example.healthcheckb10.exceptions.AlreadyExistsException;
import com.example.healthcheckb10.exceptions.BadRequestException;
import com.example.healthcheckb10.exceptions.NotFoundException;
import com.example.healthcheckb10.repositories.ApplicationRepository;
import com.example.healthcheckb10.repositories.jdbcTemplate.ApplicationDao;
import com.example.healthcheckb10.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository repository;
    private final ApplicationDao applicationDao;

    @Override
    public SimpleResponse create(ApplicationRequest request) {
        Application application = new Application();
        application.setFirstName(request.name());
        if (repository.existsByPhoneNumber(request.phoneNumber())){
            throw new AlreadyExistsException("Такой номер телефона уже существует!!!");
        }
        application.setPhoneNumber(request.phoneNumber());
        application.setCreatingApplicationDate(LocalDate.now());
        application.setProcessed(false);
        try {
            repository.save(application);
            log.info("Заявка успешна отправлена!");
            return SimpleResponse.builder()
                    .status(HttpStatus.OK)
                    .message("Заявка успешна отправлена! " +
                            "В ближайшее время с вами свяжется администратор для согласования деталей.")
                    .build();
        } catch (Exception e) {
            log.info("Произошла ошибка при отправлении заявки!");
            return SimpleResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Произошла ошибка при отправлении заявки!")
                    .build();
        }
    }

    @Override
    public SimpleResponse deleteById(List<Long>ids) {
        for (int i = 0; i < ids.size(); i++) {
            Application application = repository.findById(ids.get(i)).orElseThrow(() -> {
                log.info("Заявка под номером %s не найдена!".formatted(ids));
                return new NotFoundException("Заявка под номером %s не найдена!".formatted(ids));
            });
            if(!application.getProcessed())
                throw new BadRequestException("Нельзя удалять еще не обработанную заявку");
            repository.delete(application);
        }
        log.info("Заявка под номером %s успешно удалена!".formatted(ids));
        return SimpleResponse.builder()
                .status(HttpStatus.OK)
                .message("Заявка под номером %s успешно удалена!".formatted(ids))
                .build();
    }

    @Override
    public List<ApplicationResponse> getAllApplications() {
        List<ApplicationResponse> applications = applicationDao.getAllApplications();
        if (applications.isEmpty()) {
            log.info("Заявки не найдены!");
            return Collections.emptyList();
        }
        return applications;
    }

    @Override
    public List<SearchApplicationResponse> globalSearch(String word) {
        return applicationDao.globalSearch(word);
    }

    @Override
    public SimpleResponse manageProceeded(Long applicationId, Boolean isProceeded) {
        Application application = repository.findById(applicationId).orElseThrow(() -> {
            log.info("Заявка под номером %s не найдена!".formatted(applicationId));
            return new NotFoundException("Заявка под номером %s не найдена!".formatted(applicationId));
        });
        application.setProcessed(isProceeded);
        repository.save(application);
        if(application.getProcessed()){
            log.info("Заявка с идентификатором %s успешно обработана!".formatted(applicationId));
            return new SimpleResponse(
                    HttpStatus.OK,
                    "Заявка с идентификатором %s успешно обработана!".formatted(applicationId)
            );
        }else{
            log.info("Статус заявки с идентификатором %s успешно изменен на 'Необработанный' !".formatted(applicationId));
            return new SimpleResponse(
                    HttpStatus.OK,
                    "Статус заявки с идентификатором %s успешно изменен на 'Необработанный' !".formatted(applicationId)
            );
        }
    }
}