package com.edp.library.model.learning;

import lombok.*;

@Data
@AllArgsConstructor
public class UserApprovedLearningCount {
    private Long userId;
    private Long count;
}