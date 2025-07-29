package com.edp.careerpackage.data.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "package_template_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageTemplateSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id",nullable = false)
    private PackageTemplate template;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id",nullable = false)
    private Section section;
}
