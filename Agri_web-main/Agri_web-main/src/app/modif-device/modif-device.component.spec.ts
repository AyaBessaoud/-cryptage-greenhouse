import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifDeviceComponent } from './modif-device.component';

describe('ModifDeviceComponent', () => {
  let component: ModifDeviceComponent;
  let fixture: ComponentFixture<ModifDeviceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModifDeviceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModifDeviceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
