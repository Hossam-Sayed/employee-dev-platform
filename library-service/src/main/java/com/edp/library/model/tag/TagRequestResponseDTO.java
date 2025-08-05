package com.edp.library.model.tag;

import com.edp.library.model.enums.TagRequestStatusDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRequestResponseDTO {
    private Long id;
    private String requestedName;
    private Long requesterId; // User who requested
    private TagRequestStatusDTO status;
    private String reviewerComment; // Only if rejected
    private Long reviewerId; // Admin who reviewed
    private Instant createdAt;
    private Instant reviewedAt;
}