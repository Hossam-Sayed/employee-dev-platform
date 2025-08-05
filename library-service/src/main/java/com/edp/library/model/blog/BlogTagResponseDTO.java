package com.edp.library.model.blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogTagResponseDTO {
    private Long id; // BlogSubmissionTag ID
    private Long tagId;
    private String tagName;
    private Instant createdAt;
}