package com.edp.auth.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequestDto {

    @NotBlank(message = "refresh token cannot be blank")
    private String refreshToken;
}