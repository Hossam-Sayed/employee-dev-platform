import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagFilterDialogComponent } from './tag-filter-dialog.component';

describe('TagFilterDialogComponent', () => {
  let component: TagFilterDialogComponent;
  let fixture: ComponentFixture<TagFilterDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TagFilterDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TagFilterDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
