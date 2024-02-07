package com.example.healthcheckb10.service.serviceImpl;

import com.example.healthcheckb10.config.jwt.JwtService;
import com.example.healthcheckb10.dto.auth.AuthenticationResponse;
import com.example.healthcheckb10.dto.auth.NewPasswordRequest;
import com.example.healthcheckb10.dto.auth.SignInRequest;
import com.example.healthcheckb10.dto.auth.SignUpRequest;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.entities.User;
import com.example.healthcheckb10.entities.UserAccount;
import com.example.healthcheckb10.enums.Role;
import com.example.healthcheckb10.exceptions.AlreadyExistsException;
import com.example.healthcheckb10.exceptions.BadCredentialException;
import com.example.healthcheckb10.exceptions.NotFoundException;
import com.example.healthcheckb10.repositories.UserAccountRepository;
import com.example.healthcheckb10.repositories.UserRepository;
import com.example.healthcheckb10.service.AuthenticationService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.thymeleaf.util.StringUtils.concat;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    @Override
    public AuthenticationResponse signUp(SignUpRequest signUpRequest) {
        log.info("Регистрация новых пользователей!");
        if (userAccountRepository.existsUserAccountByEmail(signUpRequest.getEmail())) {
            log.error("Пациент с почтой %s уже существует!".formatted(signUpRequest.getEmail()));
            throw new AlreadyExistsException("Пациент с почтой %s уже существует!".formatted(signUpRequest.getEmail()));
        }
        if (userRepository.existsUserAccountByPhoneNumber(signUpRequest.getPhoneNumber())) {
            log.error("Пациент с номером %s уже существует!".formatted(signUpRequest.getPhoneNumber()));
            throw new AlreadyExistsException("Пациент с номером  %s уже существует!".formatted(signUpRequest.getPhoneNumber()));
        }
        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        UserAccount account = UserAccount.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(Role.USER)
                .user(user)
                .build();
        userRepository.save(user);
        userAccountRepository.save(account);
        log.info("Пациент с почтой %s успешно сохранен!".formatted(account.getEmail()));
        String token = jwtService.generateToken(account);
        return AuthenticationResponse.builder()
                .token(token)
                .email(account.getEmail())
                .role(account.getRole())
                .fullName(concat(user.getFirstName()," ",user.getLastName()))
                .phoneNumber(user.getPhoneNumber())
                .id(account.getId())
                .build();
    }

    @Override
    public AuthenticationResponse signIn(SignInRequest signInRequest) {
        log.info("Авторизация пациентов!");
        UserAccount account = userAccountRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> {
                    log.info("Пациент с почтой %s не найден!".formatted(signInRequest.getEmail()));
                    return new NotFoundException(
                            "Пациент с почтой %s не найден!".formatted(signInRequest.getEmail()));
                });
        User user = account.getUser();
        if (!passwordEncoder.matches(signInRequest.getPassword(), account.getPassword())) {
            log.info("Неправильный пароль!");
            throw new BadCredentialException("Неправильный пароль!");
        }
        String token = jwtService.generateToken(account);
        return AuthenticationResponse.builder()
                .token(token)
                .email(account.getEmail())
                .role(account.getRole())
                .fullName(concat(user.getFirstName()," ",user.getLastName()))
                .phoneNumber(user.getPhoneNumber())
                .id(account.getId())
                .build();
    }

    @PostConstruct
    public void initSaveAdmin() {
        User user = new User();
        user.setFirstName("Admin");
        user.setLastName("Adminov");
        user.setPhoneNumber("+996777112233");
        user.setUserAccount(
                UserAccount.builder()
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("Admin123"))
                        .role(Role.ADMIN)
                        .user(user)
                        .build());
        if (!userAccountRepository.existsUserAccountByRole(user.getUserAccount().getRole()) &&
                !userAccountRepository.existsUserAccountByEmail(user.getUserAccount().getEmail())) {
            userRepository.save(user);
            log.info("Успешно сохраненен админ методом init!");
        }
    }

    @Override
    public SimpleResponse forgotPassword(String email, String link) throws MessagingException, IOException {
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElseThrow(
            () -> new NotFoundException(
                    String.format("Пациент с таким email: %s не существует!", email)));
        String token = UUID.randomUUID().toString();
        userAccount.setResetToken(token);
        userAccountRepository.save(userAccount);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom("healthcheck05@gmail.com");
        helper.setSubject("Сброс пароля");
        helper.setTo(email);
        Resource resource = new ClassPathResource("templates/resetPassword.html");
        String htmlContent = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String formattedHtmlContent = htmlContent.replace("%s", link + "/" + token);
        helper.setText(formattedHtmlContent, true);
        javaMailSender.send(mimeMessage);
        log.info("Ссылка для сброса пароля отправлена пользователю с email : %s", email);
        return new SimpleResponse(HttpStatus.OK, String.format("Ссылка для сброса пароля отправлена пользователю с email : %s", email));
}

    @Override
    public SimpleResponse replacePassword(NewPasswordRequest newPasswordRequest) {
        log.info("Сброс пароля для токена: " + newPasswordRequest.getToken());
        try {
            UserAccount userAccount = userAccountRepository.findByResetToken(
                    newPasswordRequest.getToken()).orElseThrow(
                    () -> new NotFoundException("Нет такого пользователя!"));
            userAccount.setPassword(passwordEncoder.encode(newPasswordRequest.getNewPassword()));
            userAccount.setResetToken(null);
            userAccountRepository.save(userAccount);
            log.info("Сброс пароля успешно выполнен для токена: " + newPasswordRequest.getToken());
            return new SimpleResponse(
                    HttpStatus.OK,
                    "Успешно обновлено");
        } catch (NotFoundException e) {
            log.error("Ошибка сброса пароля для токена: " + newPasswordRequest.getToken());
            return new SimpleResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Не правильный токен!");
        }
    }

    @PostConstruct
    void init() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.
                fromStream(new ClassPathResource("healthCheck.json").getInputStream());
        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(firebaseOptions);
    }

    @Override
    public AuthenticationResponse authWithGoogle(String token) {
        log.info("Метод  аутентификации через Google стартовал ");
        FirebaseToken firebaseToken;
        try {
            firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token);
        } catch (FirebaseAuthException firebaseAuthException) {
            log.error("Во время аутентификации произошла ошибка !!!");
            throw new BadCredentialException("Во время аутентификации произошла ошибка !!!");
        }
        UserAccount userAccount;
        User user = new User();
        if (userAccountRepository.findByEmail(firebaseToken.getEmail()).isEmpty()) {
            userAccount = new UserAccount();
            String fullName = firebaseToken.getName();
            int spaceIndex = fullName.indexOf(" ");
            if (spaceIndex != -1) {
                String firstName = fullName.substring(0, spaceIndex);
                String lastName = fullName.substring(spaceIndex + 1);
                user.setFirstName(firstName);
                user.setLastName(lastName);
            } else {
                user.setFirstName(fullName);
            }
            userAccount.setEmail(firebaseToken.getEmail());
            userAccount.setPassword(passwordEncoder.encode(firebaseToken.getEmail()));
            userAccount.setRole(Role.USER);
            userAccount.setUser(user);
            userAccountRepository.save(userAccount);
            log.info("Пользователь удачно сохранен");
        }
        userAccount = userAccountRepository.findByEmail(firebaseToken.getEmail()).orElseThrow(
                () -> new NotFoundException("Пользователь с такими :" + firebaseToken.getEmail() + " не существует ."));
        String userAccountToken = jwtService.generateToken(userAccount);
        log.info("Функция  аутентификации через Google удачно завершил работу");
        return new AuthenticationResponse(userAccountToken, userAccount.getEmail(), userAccount.getRole(),
                concat(user.getFirstName()," ",user.getLastName()),user.getPhoneNumber(),userAccount.getId());
    }
}