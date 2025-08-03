import { Component } from '@angular/core';
import { endDevice } from 'app/models/endDevice';
import { EnDeviceService } from 'app/services/en-device.service';
import { NotificationsService } from 'app/services/notifications.service';
import { CommandService } from 'app/services/command.service';
import { Command } from 'app/models/command';
import { WaitCommand } from 'app/models/waitCommand';
import { WcommandService } from 'app/services/wcommand.service';
import { AuthService } from 'app/services/auth.service';
import { LogsService } from 'app/services/logs.service';
import { Logs } from 'app/models/logs';
import * as $ from 'jquery';
import 'bootstrap'; 


@Component({
  selector: 'app-get-device',
  templateUrl: './get-device.component.html',
  styleUrls: ['./get-device.component.css']
})
export class GetDeviceComponent {

  constructor( public services : EnDeviceService, private cmdService : CommandService, 
    private notificationService: NotificationsService, private wCmdService : WcommandService,
  private authService: AuthService, private logService: LogsService){}

  ngOnInit(): void {
    this.id = this.authService.CurrentUser.id;
    this.role = this.authService.CurrentUser.role;
    this.getDevices();

    setInterval(() => {
    this.getDeviceHistories();
  }, 180000); // toutes les 3 minutes
  }

  role : string ='';
  devList = [];
  id : string;
  device : endDevice = { id : '', type:'', codDevice : '', nivBat: 254, sensors :[], localActuators : [], 
    config : {pa: 1, ps : 1 , lat: 36.8816580,longt :10.3166711},
  serre :  { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []}}
  selectedDev : endDevice = { id : '', type :'', codDevice : '', nivBat: 254, sensors :[], localActuators : [], 
    config : {pa: 1, ps : 1 , lat: 36.8816580,longt :10.3166711}, serre:  { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []}}
  cmdList : Command [] = [];
  cmd : Command = {id : '', name : ''}
  waitCmd : WaitCommand = {idU: '', command : '', codDevice : '', etat : 0}
  logs: Logs[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 5;
  totalItems: number = 0;
  codDevice : String = '';
  
  getDevices(){
    this.services.getAllDevicesByUser(this.id).subscribe(res=>{
      this.devList = res;
      console.log('ress', res)
      this.getDeviceHistories()
  },err=>{
    console.log("error while fetching data.")
  });
  }

  sendCommand(cmd : Command){
    console.log("commande selectionnée", cmd)
    this.waitCmd.idU = null;
    this.waitCmd.codDevice = this.selectedDev.codDevice;
    this.waitCmd.command = cmd.name;
    this.waitCmd.etat = null;
    this.wCmdService.sendCmd(this.waitCmd).subscribe(res=>{
      console.log("res1", res)
    this.notificationService.showNotification('top', 'left', "La commande a été envoyée avec succès",2);
    $('#commandModal').modal('hide');
    })
  }

  //debut du code relatif pour la pagination
  openLogModal(dev: endDevice) {
    this.logs =[]
  this.codDevice = dev.codDevice;
  this.currentPage = 1;
  this.getLogs(this.currentPage);
  $('#logModal').modal('show');
}

getLogs(page: number): void {
  const pageIndex = page - 1; // Spring démarre à 0
  this.logService.getLogsByDevice(this.codDevice, pageIndex, this.itemsPerPage)
    .subscribe(response => {
      console.log("Réponse API logs :", response);
      this.logs = response.logs;
      this.totalItems = response.totalItems;
      // Ne pas toucher à this.currentPage ici !
    });
}

onPageChange(page: number): void {
  console.log("Changement de page détecté :", page);
  this.currentPage = page; // C'est ici qu'on met à jour la page affichée
  this.getLogs(page);
}


deleteDevice(ids: any) {
  const id: number = Number(ids);
  console.log('iddd',id);
  this.services.deleteDevices(id).subscribe(res=>{
    console.log('suppression');
  // Supprimer l'objet localement de la liste
  this.devList = this.devList.filter(device => Number(device.id) !== id);

  this.notificationService.showNotification('top', 'left', "L'ppareil a été supprimé avec succès",2);
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

SelectDevice( dev : endDevice){
  console.log(dev)
  this.selectedDev = dev;
  ($('#exampleModalDelete') as any).modal('show')
}

getBatteryIcon(nivBat: number): string {
  const percentage = (nivBat / 254) * 100;

  if (percentage == 100) {
    return 'battery_full'; // Icone verte pour 100% et plus
  } else if (percentage >= 50 && percentage < 100) {
    return 'battery_5_bar'; // Icone pour entre 50% et 100%
  } else if (percentage >= 20 && percentage < 50) {
    return 'battery_3_bar'; // Icone jaune pour entre 20% et 50%
  } else {
    return 'battery_1_bar'; // Icone rouge pour moins de 20%
  }
}

openSensorModal(device: endDevice) {
  this.selectedDev = device;
  $('#sensorModal').modal('show'); // Utilise jQuery Bootstrap modal
}

openActModal(device: endDevice){
  this.selectedDev = device;
  $('#actuatorModal').modal('show'); // Utilise jQuery Bootstrap modal 
}

openCmdModal(dev : endDevice){
  this.cmdService.getAllCommands().subscribe(res=>{
    // Filtrer la liste pour ne garder que CNFD et GETD
    this.cmdList = res.filter(cmd => cmd.name === 'CFGD' || cmd.name === 'GETD');
    this.selectedDev = dev;
  })
  $('#commandModal').modal('show'); // Utilise jQuery Bootstrap modal
}

getDeviceHistories(){
  this.services.getHistories().subscribe(res=>{
    console.log("historiques des devices", res)
    // Parcours de tous les devices pour mettre à jour nivBat 
    this.devList.forEach(device => {
      const history = res.find(h => h.codDevice === device.codDevice);
      if (history) {
        device.nivBat = history.battery; // mise à jour directe
      }
    });
  })
}

}
