import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {baseUrl} from 'environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SensorHistryService {

  constructor(private http: HttpClient) { }

  getSensorHistory(codDevice: string): Observable<any>{
    return this.http.get(`${baseUrl}/sensor/history?codDevice=${codDevice}`);
  }
}
