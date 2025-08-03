import { TestBed } from '@angular/core/testing';

import { ActHistoryService } from './act-history.service';

describe('ActHistoryService', () => {
  let service: ActHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ActHistoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
