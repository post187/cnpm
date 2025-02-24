package com.example.demo.Controller;

import com.example.demo.Service.AuthenticationService;
import com.example.demo.dto.request.APIResponse;
import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.cert.CertPathBuilder;
import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    APIResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
            var result = authenticationService.authenticate(request);
            return APIResponse.<AuthenticationResponse>builder()
                    .result(result)
                    .build();
    }
    @PostMapping("/introspect")
    APIResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return APIResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
}
