package com.edp.library.model.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagCreateRequestDTO {
    @NotBlank(message = "Tag name cannot be empty")
    @Size(min = 2, max = 50, message = "Tag name must be between 2 and 50 characters")
    private String requestedName;
}