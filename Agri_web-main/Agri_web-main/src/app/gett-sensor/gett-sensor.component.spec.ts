import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GettSensorComponent } from './gett-sensor.component';

describe('GettSensorComponent', () => {
  let component: GettSensorComponent;
  let fixture: ComponentFixture<GettSensorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GettSensorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GettSensorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
