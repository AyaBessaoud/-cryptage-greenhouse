import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import {baseUrl} from 'environments/environment';
import { GreenHouse } from 'app/models/GreenHouse';
import { ModifGreenHouse } from 'app/models/modifGreenHouse';

@Injectable({
  providedIn: 'root'
})
export class GreenHouseService {

  constructor(private http: HttpClient) { }

  getAll(id : string) : Observable<GreenHouse[]>{
    return this.http.get<GreenHouse[]>(baseUrl+'/greenHouse/user/'+id);
  }

  modifGreenHouse(serre : ModifGreenHouse, id : number): Observable<GreenHouse>{
    return this.http.put<GreenHouse>(baseUrl+'/greenHouse/'+id, serre);
  }

  addGHouse(serre : ModifGreenHouse) : Observable<GreenHouse>{
    return this.http.post<GreenHouse>(baseUrl+'/greenHouse', serre);
  }
}
