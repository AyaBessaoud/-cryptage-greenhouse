import { TestBed } from '@angular/core/testing';

import { GreenHouseService } from './green-house.service';

describe('GreenHouseService', () => {
  let service: GreenHouseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GreenHouseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
