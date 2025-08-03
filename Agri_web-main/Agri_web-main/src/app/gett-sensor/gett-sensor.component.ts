import { Component, OnInit } from '@angular/core';
import { SensorService } from 'app/services/sensor.service';
import { Sensor } from 'app/models/Sensor';
import { NotificationsService } from 'app/services/notifications.service';
import { CommandService } from 'app/services/command.service';
import { Command } from 'app/models/command';
import { WaitCommand } from 'app/models/waitCommand';
import { WcommandService } from 'app/services/wcommand.service';
import { AuthService } from 'app/services/auth.service';
import * as $ from 'jquery';
import 'bootstrap'; 

@Component({
  selector: 'app-gett-sensor',
  templateUrl: './gett-sensor.component.html',
  styleUrls: ['./gett-sensor.component.css']
})
export class GettSensorComponent {

  constructor(private sensorService : SensorService, private notifService : NotificationsService,
    private cmdService : CommandService, private wCmdService : WcommandService,
    private authService: AuthService
  ){}

  ngOnInit(){
    this.id = this.authService.CurrentUser.id;
    this.role = this.authService.CurrentUser.role;
    this.getSensors();
  }
  role : string ='';
  sensor : Sensor = {index: 0, id : '',idU : '', description: '', alertThersholdD: 0, normalThersholdD: 0, actuators: [], typeSensor: '',
  lat: 36.8816580, longt: 10.3166711, alertThersholdN: 0, normalThersholdN: 0, endDevice: { id: '', type :'', codDevice: '', nivBat: 254, sensors: [],
    localActuators: [], config: { pa: 1, ps: 1, lat: 36.8816580, longt: 10.3166711},
    serre :  { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []}
  }}
  listSensor : Sensor[] = []
  cmd : Command = {id : '', name : ''}
  waitCmd : WaitCommand = {idU: '', command : '', codDevice : '', etat : 0}
  id : string;


  getSensors(){
   this.sensorService.getSensorByUser(this.id).subscribe((res: any[]) => {
  // Replace endDevice ID with device object for each sensor
  this.listSensor = res.map(sensor => {
    return {
      ...sensor,
      endDevice: sensor.device // full device object assigned to endDevice
    };
  });
});

  }

  SelectSensor(sen: Sensor){
    this.sensor = sen;
    ($('#exampleModalDelete') as any).modal('show')
  }

  sendCommand(sen: Sensor) {
    this.cmdService.getAllCommands().subscribe(res => {
      // Trouver un seul objet avec le nom "CNFS"
      this.cmd = res.find(cmd => cmd.name === 'CFNS');
      console.log("commande CNFS", this.cmd);
      this.sensor = sen;
        this.waitCmd.etat = null;
        this.waitCmd.idU = this.sensor.idU;
        this.waitCmd.codDevice = this.sensor.endDevice.codDevice;
        this.waitCmd.command = this.cmd.name;
        console.log("àà envoyéé", this.waitCmd)
        this.wCmdService.sendCmd(this.waitCmd).subscribe(res=>{
          console.log("res1", res)
        this.notifService.showNotification('top', 'left', "La commande a été envoyée avec succès",2);
        })
    });
    
  }

  deleteSensor(ids : String){
    const id: number = Number(ids);
    this.sensorService.deleteSensor(id).subscribe(res=>{
      console.log('suppression');
    this.notifService.showNotification('top', 'left', "Le capteur a été supprimé avec succès",2);
    this.listSensor = this.listSensor.filter(sen => Number(sen.id) !== id)
    })
  }

  getSensorType(type: string): string {
    const types = {
      'temperature': 'Capteur de température',
      'lumiere': 'Capteur de lumière',
      'humidite': 'Capteur d\'humidité',
      'gaz' : 'Capteur de gaz',
      'humSol': "Capteur d\'humidité du sol"
    };
    return types[type];
  }

}
