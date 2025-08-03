import { TestBed } from '@angular/core/testing';

import { SensorHistryService } from './sensor-histry.service';

describe('SensorHistryService', () => {
  let service: SensorHistryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SensorHistryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
