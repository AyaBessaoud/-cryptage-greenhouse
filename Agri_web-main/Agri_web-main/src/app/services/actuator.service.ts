import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import {baseUrl} from 'environments/environment';
import { addActuator } from 'app/models/addActuator';
import { Actuator } from 'app/models/Actuator';
import { modifActuator } from 'app/models/modifActuator';

@Injectable({
  providedIn: 'root'
})
export class ActuatorService {

  constructor(private http: HttpClient) { }

  addActuator(actuator : addActuator ) : Observable<addActuator>{
    return this.http.post<addActuator>(baseUrl+'/actuator', actuator);
  }

  getAllActuators() : Observable<Actuator[]>{
    return this.http.get<Actuator[]>(baseUrl+'/actuator');
  }

  getAllActuatorsByUser(id : String) : Observable<Actuator[]>{
    return this.http.get<Actuator[]>(baseUrl+'/actuator/user/'+id);
  }

  deleteActuator(id : number) : Observable<Actuator>{
    return this.http.delete<Actuator>(baseUrl+'/actuator/'+id, { responseType: 'text' as 'json' });
  }

  editActuator(actuator : modifActuator) : Observable<Actuator>{
    const id : Number = Number(actuator.id)
    return this.http.put<Actuator>(baseUrl+'/actuator/'+ id, actuator);
  }

  getById(id: String) : Observable<Actuator>{
    return this.http.get<Actuator>(baseUrl+'/actuator/'+id);
  }
}
