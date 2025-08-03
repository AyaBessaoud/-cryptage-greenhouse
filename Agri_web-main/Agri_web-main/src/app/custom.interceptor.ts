import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class CustomInterceptor implements HttpInterceptor {

  constructor() {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = localStorage.getItem("Token");
    const isLoginRequest = request.url.endsWith('/employees/login');

    if (token && !isLoginRequest) {
    request = request.clone({headers: request.headers.set("Authorization", "Bearer " + token)});
    }
    return next.handle(request);
  }
  
}


/* Step | What It Does                                  

| 1  | Gets token from browser `localStorage`        |
| 2  | Skips login request from getting the token    |
| 3  | Adds `"Authorization: Bearer <token>"` header |
| 4  | Sends the modified request to the server      |*/
