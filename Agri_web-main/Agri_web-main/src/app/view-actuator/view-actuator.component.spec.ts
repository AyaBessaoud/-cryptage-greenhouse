import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewActuatorComponent } from './view-actuator.component';

describe('ViewActuatorComponent', () => {
  let component: ViewActuatorComponent;
  let fixture: ComponentFixture<ViewActuatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewActuatorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewActuatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
