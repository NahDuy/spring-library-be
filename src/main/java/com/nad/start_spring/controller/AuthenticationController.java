package com.nad.start_spring.controller;

import com.nad.start_spring.dto.request.*;
import com.nad.start_spring.dto.response.ApiResponse;
import com.nad.start_spring.dto.response.AuthenticationResponse;
import com.nad.start_spring.dto.response.EmailResponse;
import com.nad.start_spring.dto.response.IntrospectResponse;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.exception.AppException;
import com.nad.start_spring.exception.ErrorCode;
import com.nad.start_spring.repository.UserRepository;
import com.nad.start_spring.service.AuthenticationService;
import com.nad.start_spring.service.EmailService;
import com.nad.start_spring.service.util.VerificationCodeStore;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    UserRepository userRepository;
    EmailService emailService;
    VerificationCodeStore verificationCodeStore;
    PasswordEncoder passwordEncoder;
    @PostMapping("/forgot-password")
    public ApiResponse<EmailResponse> forgotPassword(@RequestBody EmailRequest emailRequest) {
        String email = emailRequest.getEmail();

        boolean exists = userRepository.existsByEmail(email);
        if (!exists) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        String code = String.valueOf((int) ((Math.random() * 900000) + 100000)); // Mã 6 chữ số
        verificationCodeStore.saveCode(email, code);
        emailService.sendVerificationCode(email, code);

        return ApiResponse.<EmailResponse>builder()
                .status(new EmailResponse(email, "Verification code sent"))
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (!verificationCodeStore.verifyCode(request.getEmail(), request.getCode())) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        verificationCodeStore.removeCode(request.getEmail());

        return ApiResponse.<String>builder()
                .status("Password changed successfully")
                .build();
    }
    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> logIn(@RequestBody AuthenticationRequest authenticationRequest) {
        var result =  authenticationService.authenticate(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .status(result)
                .build();
    }
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> logIn(@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        var result =  authenticationService.introspect(introspectRequest);
        return ApiResponse.<IntrospectResponse>builder()
                .status(result)
                .build();
    }
    @PostMapping("/logout")
    ApiResponse<Void> logOut(@RequestBody LogoutRequest logoutRequest) throws ParseException, JOSEException {
        authenticationService.logout(logoutRequest);
        return ApiResponse.<Void>builder()
                .build();
    }
    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> logOut(@RequestBody RefreshRequest refreshRequest) throws ParseException, JOSEException {
        var result =  authenticationService.refreshToken(refreshRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .status(result)
                .build();
    }
}
