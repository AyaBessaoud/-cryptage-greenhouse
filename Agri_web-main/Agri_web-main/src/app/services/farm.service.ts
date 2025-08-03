import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import {baseUrl} from 'environments/environment';
import { Farm } from 'app/models/farm';
import { AddFarm } from 'app/models/addFarm';
import { ModifFarm } from 'app/models/modifFarm';


@Injectable({
  providedIn: 'root'
})
export class FarmService {

  constructor(private http: HttpClient) { }

  getAllFarms(id: string) : Observable<Farm[]>{
    return this.http.get<Farm[]>(baseUrl+'/farm/user/'+id);
  }

  addFarm(farm : AddFarm) :Observable<Farm> {
    return this.http.post<Farm>(baseUrl+'/farm', farm);
  }

  modifFarm(farm : ModifFarm, id : number) :Observable<Farm>{
    return this.http.put<Farm>(baseUrl+'/farm/'+id, farm);
  }
}
