package com.example.healthcheckb10.service;

import com.example.healthcheckb10.dto.auth.AuthenticationResponse;
import com.example.healthcheckb10.dto.auth.NewPasswordRequest;
import com.example.healthcheckb10.dto.auth.SignInRequest;
import com.example.healthcheckb10.dto.auth.SignUpRequest;
import com.example.healthcheckb10.dto.extra.SimpleResponse;
import jakarta.mail.MessagingException;
import java.io.IOException;

public interface AuthenticationService{
    AuthenticationResponse signUp(SignUpRequest signUpRequest);
    AuthenticationResponse signIn(SignInRequest signInRequest);
    AuthenticationResponse authWithGoogle(String token);
    SimpleResponse forgotPassword(String email, String link) throws MessagingException, IOException;
    SimpleResponse replacePassword(NewPasswordRequest newPasswordRequest);
}