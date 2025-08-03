import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddActuatorComponent } from './add-actuator.component';

describe('AddActuatorComponent', () => {
  let component: AddActuatorComponent;
  let fixture: ComponentFixture<AddActuatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddActuatorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddActuatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
