
package com.edp.auth.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequestDto {

    @NotBlank(message = "username cannot be blank")
    private String username;
}
