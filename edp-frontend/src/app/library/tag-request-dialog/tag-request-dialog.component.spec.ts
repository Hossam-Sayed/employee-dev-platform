import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagRequestDialogComponent } from './tag-request-dialog.component';

describe('TagRequestDialogComponent', () => {
  let component: TagRequestDialogComponent;
  let fixture: ComponentFixture<TagRequestDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TagRequestDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TagRequestDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
