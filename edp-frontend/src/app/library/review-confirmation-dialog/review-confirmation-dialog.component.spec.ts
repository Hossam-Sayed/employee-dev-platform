import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewConfirmationDialogComponent } from './review-confirmation-dialog.component';

describe('ReviewConfirmationDialogComponent', () => {
  let component: ReviewConfirmationDialogComponent;
  let fixture: ComponentFixture<ReviewConfirmationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewConfirmationDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewConfirmationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
