import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifSensorComponent } from './modif-sensor.component';

describe('ModifSensorComponent', () => {
  let component: ModifSensorComponent;
  let fixture: ComponentFixture<ModifSensorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModifSensorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModifSensorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
