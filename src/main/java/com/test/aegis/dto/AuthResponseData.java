package com.test.aegis.dto;

import lombok.Data;

@Data
public class AuthResponseData {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    public AuthResponseData(
            String accessToken,
            String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
