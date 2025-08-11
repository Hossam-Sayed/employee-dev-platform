package com.edp.careerpackage.data.entity;

import com.edp.careerpackage.data.enums.CriteriaType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "template_section_required_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateSectionRequiredTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "package_template_section_id", nullable = false)
    private PackageTemplateSection packageTemplateSection;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_type", nullable = false)
    private CriteriaType criteriaType;

    @Column(name = "criteria_min_value", nullable = false)
    private Double criteriaMinValue;
}
