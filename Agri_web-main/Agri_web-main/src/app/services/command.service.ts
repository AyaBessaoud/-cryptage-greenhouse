import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { baseUrl } from 'environments/environment';
import { Command } from 'app/models/command';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CommandService {

  constructor(private http: HttpClient ) { }

  public getAllCommands() : Observable<Command[]> {
    return this.http.get<Command[]>(baseUrl+'/command');
  }
}
