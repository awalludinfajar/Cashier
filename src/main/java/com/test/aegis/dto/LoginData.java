package com.test.aegis.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginData implements Serializable {

    @NotEmpty(message = "User Is Required")
    private String username;

    @NotEmpty(message = "Password Is Required")
    private String password;
}
