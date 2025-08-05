import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagRequestDetailsDialogComponent } from './tag-request-details-dialog.component';

describe('TagRequestDetailsDialogComponent', () => {
  let component: TagRequestDetailsDialogComponent;
  let fixture: ComponentFixture<TagRequestDetailsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TagRequestDetailsDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TagRequestDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
