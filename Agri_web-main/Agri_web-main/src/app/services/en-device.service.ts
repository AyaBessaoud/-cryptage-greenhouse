import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import {baseUrl} from 'environments/environment';
import { addDevice } from 'app/models/addDevice';
import { endDevice } from 'app/models/endDevice';
import { ModifDevice } from 'app/models/modifDevice';
import { DeviceHistory } from 'app/models/deviceHistory';

@Injectable({
  providedIn: 'root'
})
export class EnDeviceService {

  constructor(private http: HttpClient) { }

  addDevice(device : addDevice): Observable<endDevice> {
      return this.http.post<endDevice>(baseUrl+'/endDevice',device);
  }

  getAllDevices(): Observable<endDevice[]>{
      return this.http.get<endDevice[]>(baseUrl+'/endDevice');
  }

  getAllDevicesByUser(id : string): Observable<endDevice[]>{
      return this.http.get<endDevice[]>(baseUrl+'/endDevice/user/'+id);
  }

  deleteDevices(id : number): Observable<endDevice>{
   return this.http.delete<endDevice>(baseUrl+'/endDevice/'+ id, { responseType: 'text' as 'json' });
  }
  
  getDeviceById(id : String) : Observable<addDevice>{
    return this.http.get<endDevice>(baseUrl+'/endDevice/'+ id);
  }

  editDevice(dev : ModifDevice, id : number) : Observable<endDevice>{
    return this.http.put<endDevice>(baseUrl+'/endDevice/'+id , dev);
  }

  getHistories() : Observable<DeviceHistory[]>{
    return this.http.get<DeviceHistory[]>(baseUrl+'/deviceHistory')
  }

}
