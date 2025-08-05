import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MaterialHistoryComponent } from './material-history.component';

describe('MaterialHistoryComponent', () => {
  let component: MaterialHistoryComponent;
  let fixture: ComponentFixture<MaterialHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MaterialHistoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MaterialHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
