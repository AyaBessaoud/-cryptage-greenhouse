import { TestBed } from '@angular/core/testing';

import { EnDeviceService } from './en-device.service';

describe('EnDeviceService', () => {
  let service: EnDeviceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EnDeviceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
