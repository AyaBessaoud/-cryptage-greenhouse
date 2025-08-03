import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthRequest } from 'app/models/authRequest';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { baseUrl } from 'environments/environment';
import { Employee } from 'app/models/employee';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private router : Router) { }

  CurrentUser : Employee = {lastName: '', firstName: '', address: '', mobile: '', email : '', id :'' , role : '',
    serre:[], ferme : [], password : ''
  };

  logIn(form : AuthRequest) : Observable<AuthRequest>{
    return this.http.post<AuthRequest>(baseUrl+"/employees/login",form);
  }

  logOut(){
    localStorage.removeItem("Token");
    localStorage.removeItem("currentUser");
    this.router.navigateByUrl('');
  }

  getCurrentUser(){
    this.CurrentUser = JSON.parse(localStorage.getItem("currentUser")!); 
  }

  setCurrentUser(user:Employee){
    localStorage.setItem("currentUser", JSON.stringify(user));
    this.getCurrentUser();
  }

}
