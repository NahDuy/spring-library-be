package org.infomation.spring.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.infomation.spring.dto.request.AuthenticationRequest;
import org.infomation.spring.dto.request.IntrospectRequest;
import org.infomation.spring.dto.request.RefreshRequest;
import org.infomation.spring.dto.response.AuthenticationResponse;
import org.infomation.spring.dto.response.IntrospectResponse;
import org.infomation.spring.entity.InvalidatedToken;
import org.infomation.spring.entity.User;
import org.infomation.spring.repository.InvalidatedTokenRepository;
import org.infomation.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@Slf4j
public class AuthenticationService {
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Value("${jwt.valid-duration}")
    private long validDuration;

    @Value("${jwt.refresh-duration}")
    private long refreshDuration;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InvalidatedTokenRepository tokenRepository;


    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (Exception e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        var user =userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Cannot find user"));

        boolean authenticated = request.getPassword().equals(user.getPassword());
        if (!authenticated)
            throw new RuntimeException("Authentication failed");
        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(),true);
        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        tokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user =
                userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException(""));

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .subject(user.getUsername())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(validDuration, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .claim("scope", buildScope(user))

                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(refreshDuration, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) throw new RuntimeException("Token expired");
        if (tokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new RuntimeException("Token expired");
        return signedJWT;
    }
    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(stringJoiner::add);

        return stringJoiner.toString();
    }
}
