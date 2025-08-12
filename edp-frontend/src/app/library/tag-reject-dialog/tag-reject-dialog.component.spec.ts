import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagRejectDialogComponent } from './tag-reject-dialog.component';

describe('TagRejectDialogComponent', () => {
  let component: TagRejectDialogComponent;
  let fixture: ComponentFixture<TagRejectDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TagRejectDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TagRejectDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
