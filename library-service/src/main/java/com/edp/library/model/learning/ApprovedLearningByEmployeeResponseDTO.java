package com.edp.library.model.learning;

import lombok.Data;

@Data
public class ApprovedLearningByEmployeeResponseDTO {
    private String username;
    private Long approvedLearningsCount;
}