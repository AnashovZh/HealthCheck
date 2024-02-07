package com.example.healthcheckb10.api;

import com.example.healthcheckb10.dto.auth.AuthenticationResponse;
import com.example.healthcheckb10.dto.auth.NewPasswordRequest;
import com.example.healthcheckb10.dto.auth.SignInRequest;
import com.example.healthcheckb10.dto.auth.SignUpRequest;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import com.example.healthcheckb10.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthApi {
    private final AuthenticationService service;

    @PostMapping("/signUp")
    @Operation(
            summary = "Метод для регистрации новых пользователей!",
            description = "Права на метод имеют все!")
    public AuthenticationResponse singUp(@RequestBody @Valid SignUpRequest request) {
        return service.signUp(request);
    }

    @PostMapping("/signIn")
    @Operation(
            summary = "Метод для авторизации(проверка подлинности)пользователей, сохраненных в базе данных!",
            description = "Права на метод имеют все!")
    public AuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return service.signIn(request);
    }

    @PutMapping("/forgot-password")
    @Operation(
            summary = "Забыли пароль",
            description = "На почту приходит ссылка для сброса пароля!"
    )
    public SimpleResponse forgotPassword(@RequestParam String email,
                                         @RequestParam String link) throws MessagingException, IOException {
        return service.forgotPassword(email, link);
    }

    @PutMapping("/replace-password")
    @Operation(
            summary = "Метод для сброса пароля пользователя"
    )
    public SimpleResponse replacePassword(@RequestBody @Valid NewPasswordRequest newPasswordRequest) {
        return service.replacePassword(newPasswordRequest);
    }

    @PostMapping("/google")
    @Operation(
            summary = "Метод для авторизации пользователей через Google ",
            description = "Права на метод имеют все !")
    public AuthenticationResponse authResponse(@RequestParam String token) {
        return service.authWithGoogle(token);
    }
}