package com.edp.library.model.tag;

import com.edp.library.model.enums.TagRequestStatusDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRequestReviewDTO {
    @NotNull(message = "Status cannot be null")
    private TagRequestStatusDTO status; // Should be APPROVED or REJECTED

    private String reviewerComment;
}