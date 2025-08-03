import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetActuatorComponent } from './get-actuator.component';

describe('GetActuatorComponent', () => {
  let component: GetActuatorComponent;
  let fixture: ComponentFixture<GetActuatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GetActuatorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GetActuatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
