package org.infomation.spring.controller;

import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.infomation.spring.dto.request.AuthenticationRequest;
import org.infomation.spring.dto.request.IntrospectRequest;
import org.infomation.spring.dto.request.RefreshRequest;
import org.infomation.spring.dto.response.AuthenticationResponse;
import org.infomation.spring.dto.response.IntrospectResponse;
import org.infomation.spring.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/token")
    AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request){
        return authenticationService.authenticate(request);
    }
    @PostMapping("/introspect")
    IntrospectResponse authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        return authenticationService.introspect(request);
    }
    @PostMapping("/refresh")
    AuthenticationResponse authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        return authenticationService.refreshToken(request);
    }
}