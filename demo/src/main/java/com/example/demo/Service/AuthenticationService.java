package com.example.demo.Service;

import com.example.demo.Entity.User;
import com.example.demo.Exception.AppException;
import com.example.demo.Exception.ErrorCode;
import com.example.demo.Respository.UserRepository;
import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.IntrospectResponse;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected  String SIGNER_KEY;
    PasswordEncoder passwordEncoder;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        boolean authenticated =  passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        try {
            var token = generateToken(user);
            return AuthenticationResponse.builder()
                    .token(token)
                    .authenticated(true)
                    .build();
        } catch (KeyLengthException e) {
            throw new RuntimeException(e);
        }

    }
    private String generateToken(User user) throws KeyLengthException {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("dev")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }

    }

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        var verified = signedJWT.verify(jwsVerifier);

        Date timeToken = signedJWT.getJWTClaimsSet().getExpirationTime();

        return IntrospectResponse.builder()
                .valid(verified && timeToken.after(new Date()))
                .build();

    }
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
             user.getRoles().forEach(stringJoiner::add);
        }
        return stringJoiner.toString();
    }
}
