import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetFarmComponent } from './get-farm.component';

describe('GetFarmComponent', () => {
  let component: GetFarmComponent;
  let fixture: ComponentFixture<GetFarmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GetFarmComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GetFarmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
