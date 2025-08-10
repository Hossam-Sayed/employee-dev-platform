package com.edp.tag.model.tag;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponseDto {

    private Long id;
    private String name;
    private Instant createdAt;
    private Long createdBy;
}
