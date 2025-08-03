import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifActuatorComponent } from './modif-actuator.component';

describe('ModifActuatorComponent', () => {
  let component: ModifActuatorComponent;
  let fixture: ComponentFixture<ModifActuatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModifActuatorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModifActuatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
