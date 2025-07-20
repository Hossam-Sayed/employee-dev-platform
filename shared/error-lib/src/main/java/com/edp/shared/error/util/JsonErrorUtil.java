package com.edp.shared.error.util;

import com.edp.shared.error.model.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

public class JsonErrorUtil {


    private static ObjectMapper objectMapper;

    public static void setObjectMapper(ObjectMapper customMapper) {
        objectMapper = customMapper;
    }

    public static String toJsonError(int status, String error, String message, String path, LocalDateTime timestamp) {
        try {
            ErrorResponse response = ErrorResponse.builder()
                    .status(status)
                    .error(error)
                    .message(message)
                    .path(path)
                    .timestamp(timestamp)
                    .build();

            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return "{\"error\":\"Unexpected serialization error: " + e.getMessage() + "\"}";
        }
    }
}
