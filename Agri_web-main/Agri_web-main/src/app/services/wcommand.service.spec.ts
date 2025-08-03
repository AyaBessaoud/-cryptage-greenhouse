import { TestBed } from '@angular/core/testing';

import { WcommandService } from './wcommand.service';

describe('WcommandService', () => {
  let service: WcommandService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WcommandService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
