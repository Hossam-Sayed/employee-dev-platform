package com.edp.library.model.wiki;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WikiTagResponseDTO {
    private Long id; // WikiSubmissionTag ID
    private Long tagId;
    private String tagName;
    private Instant createdAt;
}