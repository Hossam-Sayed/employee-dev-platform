package com.edp.auth.model;

import lombok.Data;

@Data
public class RefreshRequestDto {
    private String refreshToken;
}