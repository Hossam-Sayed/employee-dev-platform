package com.edp.library.model.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagUpdateStatusDTO {
    @NotNull(message = "Active status cannot be null")
    private Boolean active;
}