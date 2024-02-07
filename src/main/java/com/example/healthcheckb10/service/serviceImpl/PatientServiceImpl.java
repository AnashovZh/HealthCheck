package com.example.healthcheckb10.service.serviceImpl;

import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.dto.user.response.PatientGetByIdResponse;
import com.example.healthcheckb10.dto.user.response.PatientResponse;
import com.example.healthcheckb10.entities.User;
import com.example.healthcheckb10.exceptions.BadRequestException;
import com.example.healthcheckb10.exceptions.NotFoundException;
import com.example.healthcheckb10.dto.user.request.UserRequest;
import com.example.healthcheckb10.dto.user.response.ProfileResponse;
import com.example.healthcheckb10.entities.UserAccount;
import com.example.healthcheckb10.exceptions.AlreadyExistsException;
import com.example.healthcheckb10.exceptions.BadCredentialException;
import com.example.healthcheckb10.repositories.UserAccountRepository;
import com.example.healthcheckb10.repositories.UserRepository;
import com.example.healthcheckb10.repositories.jdbcTemplate.PatientDao;
import com.example.healthcheckb10.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class PatientServiceImpl implements PatientService {
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final PatientDao patientDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PatientGetByIdResponse getPatientById(Long id) {
        return patientDao.getPatientById(id).orElseThrow(() -> {
            log.info("Пациент под идентификатором  %s не найден!".formatted(id));
            return new NotFoundException("Пациент под идентификатором  %s не найден!".formatted(id));
        });
    }

    @Override
    public List<PatientResponse> getAllPatients(String word) {
        if (word != null) {
            return patientDao.getAllPatientsBySearch(word);
        }
        return patientDao.getAllPatients();
    }

    @Override
    public SimpleResponse deletePatientById(Long id) {
        if (id == 1L) {
            throw new BadRequestException("Вы пытаетесь удалить админа!");
        }
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.info("Пациент под идентификатором  %s не найден!".formatted(id));
            return new NotFoundException("Пациент под идентификатором  %s не найден!".formatted(id));
        });
        userRepository.delete(user);
        log.info("Пациент под идентификатором %s успешно удален!".formatted(id));
        return SimpleResponse.builder()
                .status(HttpStatus.OK)
                .message("Пациент под идентификатором %s успешно удален!".formatted(id))
                .build();
    }

    @Override
    public ProfileResponse getProfile() {
        log.info("Метод getProfile успешно стартовал");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Пользователь с почтой :" + email + " не существует !"));
        User user = userAccount.getUser();
        log.info("Метод getProfile успешно завершен");
        return ProfileResponse.build(user, userAccount);
    }

    @Override
    public ProfileResponse updateProfile(UserRequest userRequest) {
        log.info("Метод updateProfile успешно стартовал");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Пользователь с почтой %s не найден".formatted(userRequest.email())));
        User user = userAccount.getUser();
        user.setFirstName(userRequest.firstName());
        user.setLastName(userRequest.lastName());
        if (!userRequest.email().equals(email)) {
            if (userAccountRepository.existsUserAccountByEmail(userRequest.email())) {
                throw new AlreadyExistsException("Почтовый адрес: %s уже существует !"
                        .formatted(userRequest.email()));
            }
        }
        userAccount.setEmail(userRequest.email());
        if (!userRequest.phoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsUserAccountByPhoneNumber(userRequest.phoneNumber())) {
                throw new AlreadyExistsException("Телефонный номер: %s уже существует !"
                        .formatted(userRequest.phoneNumber()));
            }
        }
        user.setPhoneNumber(userRequest.phoneNumber());
        userAccount.setUser(user);
        userAccountRepository.save(userAccount);
        log.info("Метод update успешно завершен");
        return ProfileResponse.build(user, userAccount);
    }

    @Override
    public SimpleResponse updatePassword(String oldPassword, String newPassword) {
        log.info("Метод updatePassword успешно стартовал");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("Пользователь с почтой %s не найден".formatted(email)));
        String passwordUserAccount = userAccount.getPassword();
        if (BCrypt.checkpw(oldPassword, passwordUserAccount)) {
            if (oldPassword.equals(newPassword)) {
                throw new BadCredentialException("Вы вводите нынешний пароль!");
            }
            userAccount.setPassword(passwordEncoder.encode(newPassword));
        } else {
            throw new BadCredentialException("Неверный старый пароль ☺");
        }
        userAccountRepository.save(userAccount);
        return SimpleResponse.builder()
                .status(OK)
                .message("Ваш пароль успешно изменен")
                .build();
    }
}