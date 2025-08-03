import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { WaitCommand } from 'app/models/waitCommand';
import { baseUrl } from 'environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WcommandService {

  constructor(private http : HttpClient) { }

  public sendCmd(cmd : WaitCommand) : Observable<WaitCommand>{
  
    return this.http.post<WaitCommand>(baseUrl+'/send_command', cmd)
   
  }
}
