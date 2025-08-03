import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import {baseUrl} from 'environments/environment';
import { CreatePlanification } from 'app/models/createPlanification';
import { Planification } from 'app/models/Planification';
import { ModifPlanification } from 'app/models/modifPlanif';

@Injectable({
  providedIn: 'root'
})
export class PlanificationService {

  constructor(private http: HttpClient) { }

  createPlanif(planif :CreatePlanification) : Observable<Planification>{
    return this.http.post<Planification>(baseUrl+'/planification',planif);
  }

  getPlanification(): Observable<Planification[]>{
    return this.http.get<Planification[]>(baseUrl+'/planification');
  }

  putPlanification(planif : ModifPlanification, id : number) : Observable<Planification>{
    return this.http.put<Planification>(baseUrl+'/planification/'+id, planif);
  }

  getById(id : string) : Observable<Planification>{
    return this.http.get<Planification>(baseUrl+'/planification/'+id)
  }
}
