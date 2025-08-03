import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { baseUrl } from 'environments/environment';
import { ActuatorHistory } from 'app/models/actuatorHistory';

@Injectable({
  providedIn: 'root'
})
export class ActHistoryService {

  constructor(private http: HttpClient) { }

  public getAllHistory() : Observable<ActuatorHistory[]>{
    return this.http.get<ActuatorHistory[]>(baseUrl+'/actuatorHistory')
  }
}
