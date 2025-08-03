package com.edp.careerpackage.model.sectionprogress;

import com.edp.careerpackage.model.tagprogress.TagPogressResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionProgressResponseDto {

    private Long sectionProgressId;
    private String sectionName;
    private String sectionDescription;
    private Double sectionProgressPercent;
    private List<TagPogressResponseDto> tags;
}