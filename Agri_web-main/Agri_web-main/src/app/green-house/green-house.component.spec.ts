import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GreenHouseComponent } from './green-house.component';

describe('GreenHouseComponent', () => {
  let component: GreenHouseComponent;
  let fixture: ComponentFixture<GreenHouseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GreenHouseComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GreenHouseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
