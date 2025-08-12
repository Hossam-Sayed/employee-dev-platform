import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PendingTagRequestsComponent } from './pending-tag-requests.component';

describe('PendingTagRequestsComponent', () => {
  let component: PendingTagRequestsComponent;
  let fixture: ComponentFixture<PendingTagRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PendingTagRequestsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PendingTagRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
