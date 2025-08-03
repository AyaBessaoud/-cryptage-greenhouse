import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import {baseUrl} from 'environments/environment';
import { AddSensor } from 'app/models/AddSensor';
import { Sensor } from 'app/models/Sensor';
import { ModifSensor } from 'app/models/modifSensor';

@Injectable({
  providedIn: 'root'
})
export class SensorService {

  constructor(private http: HttpClient) { }

  addSensor(sensor : AddSensor) : Observable<AddSensor> {
    return this.http.post<AddSensor>(baseUrl+'/sensor',sensor);
  }

  getAllSensors() : Observable<Sensor[]> {
    return this.http.get<Sensor[]>(baseUrl+'/sensor');
  }

  getSensorByUser(id: string): Observable<Sensor[]> {
    return this.http.get<Sensor[]>(baseUrl+'/sensor/user/'+id);
  }

  deleteSensor(id : number) : Observable<Sensor> {
    return this.http.delete<Sensor>(baseUrl+'/sensor/'+ id , { responseType: 'text' as 'json' });
  }

  getSensorById(id : String) : Observable<Sensor>{
    return this.http.get<Sensor>(baseUrl+'/sensor/' + id)
  }

  editSensor(sensor : ModifSensor, ids : String) : Observable<ModifSensor>{
    const id : Number = Number(ids)
    return this.http.put<ModifSensor>(baseUrl+'/sensor/'+id, sensor)
  }
}
