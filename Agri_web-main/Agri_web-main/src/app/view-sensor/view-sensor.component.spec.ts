import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewSensorComponent } from './view-sensor.component';

describe('ViewSensorComponent', () => {
  let component: ViewSensorComponent;
  let fixture: ComponentFixture<ViewSensorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewSensorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewSensorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
