import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {baseUrl} from 'environments/environment';
import { HttpClient } from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})
export class LogsService {

  constructor(private http: HttpClient) { }

  getLogsByDevice(code: String, page: number, size: number): Observable<any> {
    return this.http.get<any>(`${baseUrl}/logs/${code}?page=${page}&size=${size}`);
  }

  getDeviceHistory(codDevice: string): Observable<any> {
  return this.http.get(`${baseUrl}/logs/history/${codDevice}`);
  }

getHistoryBySerre(id : string): Observable<any>{
  return this.http.get(`${baseUrl}/logs/history/serre/${id}`);
}

}
