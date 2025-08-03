import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetPlanificationComponent } from './get-planification.component';

describe('GetPlanificationComponent', () => {
  let component: GetPlanificationComponent;
  let fixture: ComponentFixture<GetPlanificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GetPlanificationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GetPlanificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
