package com.test.aegis.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.aegis.config.JwtUtilsConfig;
import com.test.aegis.dto.AuthData;
import com.test.aegis.dto.AuthResponseData;
import com.test.aegis.dto.LoginData;
import com.test.aegis.dto.ResponseData;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtUtilsConfig jwtUtilsConfig;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public AuthController(JwtUtilsConfig jwtUtilsConfig, AuthenticationManager authenticationManager, ModelMapper modelMapper) {
        this.jwtUtilsConfig = jwtUtilsConfig;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
    }
    
    @PostMapping("/login")
    public ResponseEntity<ResponseData<AuthResponseData>> login(@Validated @RequestBody LoginData loginData, Errors errors) {
        ResponseData<AuthResponseData> data = new ResponseData<>();
        Map<String, Object> additional = new HashMap<>();

        if (errors.hasErrors()) {
            List<String> errorMessages = errors.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());

            data.setSuccess(false);
            data.setMessage("Login failed with " + errorMessages.size() + " errors.");
            data.setMessages(errorMessages);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            AuthData principal = (AuthData) authentication.getPrincipal();
    
            additional.put("userId", principal.getId());
            additional.put("fullname", principal.getFullName());
            additional.put("username", principal.getUsername());
            additional.put("role", principal.getRole());
    
            String token = jwtUtilsConfig.generateJwtToken(authentication, additional);
            String refreshToken = jwtUtilsConfig.generateRefreshJwtToken(authentication, additional);
    
            data.setSuccess(true);
            data.setList(null);
            data.setData(new AuthResponseData(token, refreshToken));
            return ResponseEntity.ok().body(data);
            
        } catch (AuthenticationException ex) {
            data.setSuccess(false);
            data.setMessage("Login failed: Invalid username or password.");
            data.setMessages(List.of("Invalid username or password."));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data);
        }
    }
}
