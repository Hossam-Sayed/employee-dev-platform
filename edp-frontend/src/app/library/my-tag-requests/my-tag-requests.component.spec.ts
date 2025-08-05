import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyTagRequestsComponent } from './my-tag-requests.component';

describe('MyTagRequestsComponent', () => {
  let component: MyTagRequestsComponent;
  let fixture: ComponentFixture<MyTagRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyTagRequestsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyTagRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
