import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { AddEmployee } from 'app/models/addEmployee';
import { Observable } from 'rxjs';
import { Employee } from 'app/models/employee';
import {baseUrl} from 'environments/environment';
import { ModifEmployee } from 'app/models/modifEmp';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  constructor(private http: HttpClient) { }

  addEmployee(emp : AddEmployee): Observable<AddEmployee> {
    return this.http.post<AddEmployee>(baseUrl+'/employees',emp);
  }

  getAllEmployee(): Observable<Employee[]>{
    return this.http.get<Employee[]>(baseUrl+'/employees');
  }

  deleteEmp(id : number) : Observable<Employee> {
    return this.http.delete<Employee>(baseUrl+ '/employees/' + id,{ responseType: 'text' as 'json' })
  }

  getEmployeeById( id : string) : Observable<Employee> {
    return this.http.get<Employee>(baseUrl+'/employees/' + id);
  }

  editEmp(emp : ModifEmployee, id : number) : Observable<Employee>{
   return this.http.put<Employee>(baseUrl+'/employees/' + id, emp)
  }
}
