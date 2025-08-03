import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifPlanificationComponent } from './modif-planification.component';

describe('ModifPlanificationComponent', () => {
  let component: ModifPlanificationComponent;
  let fixture: ComponentFixture<ModifPlanificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModifPlanificationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModifPlanificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
