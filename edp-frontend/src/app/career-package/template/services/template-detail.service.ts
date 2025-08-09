import { Injectable, signal } from '@angular/core';
import { TemplateDetailResponseDto } from '../models/template-detail-response.dto';
import { TemplateSectionResponseDto } from '../models/template-section-response.dto';
import { TemplateSectionRequiredTagResponseDto } from '../models/template-section-required-tag.dto';

@Injectable({ providedIn: 'root' })
export class TemplateDetailService {
  private _template = signal<TemplateDetailResponseDto | null>(null);

  template = this._template.asReadonly();

  setTemplate(template: TemplateDetailResponseDto) {
    this._template.set(template);
  }

  updateDepartment(department: string) {
    this._template.update((t) => (t ? { ...t, department } : t));
  }

  updatePosition(position: string) {
    this._template.update((t) => (t ? { ...t, position } : t));
  }

  removeTag(sectionId: number, tagId: number) {
    this._template.update((t) => {
      if (!t) return t;
      return {
        ...t,
        sections: t.sections.map((s) =>
          s.id === sectionId
            ? {
                ...s,
                requiredTags: s.requiredTags.filter((tag) => tag.id !== tagId),
              }
            : s
        ),
      };
    });
  }
  removeSection(sectionId: number) {
    this._template.update((t) => {
      if (!t) return t;
      return {
        ...t,
        sections: t.sections.filter((s) => s.id !== sectionId),
      };
    });
  }

  addSection(section: TemplateSectionResponseDto) {
    this._template.update((t) => {
      if (!t) return t;
      return {
        ...t,
        sections: [...t.sections, section],
      };
    });
  }

  addTag(sectionId: number, tag: TemplateSectionRequiredTagResponseDto) {
    this._template.update((t) => {
      if (!t) return t;
      return {
        ...t,
        sections: t.sections.map((s) =>
          s.id === sectionId
            ? {
                ...s,
                requiredTags:
                  s.requiredTags === null ? [tag] : [...s.requiredTags, tag],
              }
            : s
        ),
      };
    });
  }
}
