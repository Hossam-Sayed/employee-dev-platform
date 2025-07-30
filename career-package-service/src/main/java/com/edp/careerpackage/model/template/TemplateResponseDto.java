package com.edp.careerpackage.model.template;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateResponseDto {

    private Long id;
    private String department;
    private String position;
    private LocalDateTime createdAt;
}
