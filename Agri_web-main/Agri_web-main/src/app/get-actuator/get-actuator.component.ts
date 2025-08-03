import { Component } from '@angular/core';
import { EnDeviceService } from 'app/services/en-device.service';
import { NotificationsService } from 'app/services/notifications.service';
import { ActuatorService } from 'app/services/actuator.service';
import { Actuator } from 'app/models/Actuator';
import { CommandService } from 'app/services/command.service';
import { Command } from 'app/models/command';
import { WaitCommand } from 'app/models/waitCommand';
import { WcommandService } from 'app/services/wcommand.service';
import { ActHistoryService } from 'app/services/act-history.service';
import { ActuatorHistory } from 'app/models/actuatorHistory';
import { AuthService } from 'app/services/auth.service';
import { baseUrl } from 'environments/environment';
import { Client, IMessage, Stomp } from '@stomp/stompjs';  //servent à gérer la connexion et les messages STOMP.
import * as SockJS from 'sockjs-client';  //crée une connexion WebSocket
import * as $ from 'jquery';
import 'bootstrap'; 

@Component({
  selector: 'app-get-actuator',
  templateUrl: './get-actuator.component.html',
  styleUrls: ['./get-actuator.component.css']
})
export class GetActuatorComponent {

  constructor(private cmdService : CommandService, private actService: ActuatorService,
    private notifService: NotificationsService, private wCmdService : WcommandService,
    private actHistory : ActHistoryService, private authService: AuthService) { }

    ngOnInit(){
      this.id = this.authService.CurrentUser.id;
      this.role = this.authService.CurrentUser.role;
      this.getActuator();
    }

    act : Actuator =  {idU:'', description : '',id : '', index :0, output : '', longt : 10.3166711 , lat : 36.8816580 , 
    sensors: [], device : {id : '',type: '', codDevice : '', nivBat: 254, sensors :[], localActuators : [], 
      config : {pa: 1, ps : 1 , lat: 36.8816580,longt :10.3166711}, 
    serre :  { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []}}}
    acts : Actuator[] = [];
    cmd : Command[] = []
    waitCmd : WaitCommand = {idU: '', command : '', codDevice : '', etat : 0}
    listHistory : ActuatorHistory[] = []
    historyMap: Map<string, number> = new Map();  //associer une clé (idU) avec une valeur (etat)
    id : string;
    role : string ='';
    

    
    getActuator(){
      this.actService.getAllActuatorsByUser(this.id).subscribe(res =>{
        this.acts = res;
        // Remplir codDevices à partir des objets reçus
        this.getHistories()
        this.connectWebSocket();
      })
    }

    getHistories(){
      this.actHistory.getAllHistory().subscribe(res=>{
        this.listHistory = res;
        console.log("res22", res)
        this.historyMap.clear();
        res.forEach(history => {
          this.historyMap.set(history.idU, history.etat);
           this.pendingUpdates.delete(history.idU); // confirme la donnée
        });
      })
    }

    SelectActuator(act: Actuator){
        this.act = act;
        ($('#exampleModalDelete') as any).modal('show')
    }

    deleteAct(ids : string){
      const id : number = Number(ids) 
      this.actService.deleteActuator(id).subscribe(res=>{
        this.notifService.showNotification('top', 'left', "L'actionneur a été supprimé avec succès",2);
        this.acts = this.acts.filter(act => Number(act.id) !== id)
      })
    }

  sendCommand(act : Actuator, cmd : string){
    this.act = act;
    this.waitCmd.idU = this.act.idU;
    this.waitCmd.codDevice = this.act.device.codDevice;
    this.waitCmd.command = cmd;
    console.log("wait command",this.waitCmd )
    this.wCmdService.sendCmd(this.waitCmd).subscribe(res=>{
      console.log("res1", res)
    this.notifService.showNotification('top', 'left', "La commande a été envoyée avec succès",2);
    })
  }

  pendingUpdates: Set<string> = new Set(); // contient les idU en attente de confirmation
  onToggle(event: any, act: Actuator) {
    const isChecked = event.target.checked; // true ou false
    const value = isChecked ? 1 : 0;
    console.log(`Switch pour ${act.idU} est`, isChecked ? 'activé' : 'désactivé');
    this.act = act;
    this.waitCmd.idU = this.act.idU;
    this.waitCmd.codDevice = this.act.device.codDevice;
    this.waitCmd.etat = value;
     console.log("etat" , value)
    this.waitCmd.command = 'DOA';
    console.log("wait command",this.waitCmd )
    this.pendingUpdates.add(act.idU); //// Ajoute à la liste des mises à jour en attente
    
    this.wCmdService.sendCmd(this.waitCmd).subscribe(res=>{
      console.log("res1", res)

    this.notifService.showNotification('top', 'left', "La commande a été envoyée avec succès",2);
    this.historyMap.set(act.idU, value); //// Met à jour localement l'état dans le map
    })
  }
  
  private stompClient: Client;

connectWebSocket() {
  const socket = new SockJS(baseUrl + '/notif');
  this.stompClient = Stomp.over(() => socket);

  this.stompClient.onConnect = () => {
    this.stompClient.subscribe('/topic/actHistoy', message => {
      const obj = JSON.parse(message.body);
      console.log("objj", obj)

      // Vérifie si l'objet a un idU valide
      if (obj && obj.idU) {
        // Cherche l'index de l'historique existant avec le même idU
        const idx = this.listHistory.findIndex(item => item.idU === obj.idU);

        if (idx !== -1) {
          console.log("objet trouvé", obj)
          // Mise à jour de l'historique existant
          this.listHistory[idx] = obj;
          this.pendingUpdates.delete(obj.idU); // confirmation reçue
          console.log("pending", this.pendingUpdates)
          // Mise à jour du historyMap avec la nouvelle valeur d'état
          this.historyMap.set(obj.idU, obj.etat);
        }

        
      }
    });
  };

  this.stompClient.onStompError = (frame) => {
    console.error('Erreur STOMP :', frame.headers['message'], frame.body);
  };

  this.stompClient.onWebSocketClose = () => {
    console.warn("WebSocket déconnecté, tentative de reconnexion dans 5s...");
    this.scheduleReconnect();
  };

  this.stompClient.onWebSocketError = (event) => {
    console.error("Erreur WebSocket : ", event);
    this.scheduleReconnect();
  };

  this.stompClient.activate();
}

private reconnectTimeout: any;
private reconnectDelay = 5000; // 5 secondes
private scheduleReconnect() {
  if (this.reconnectTimeout) {
    clearTimeout(this.reconnectTimeout);
  }

  this.reconnectTimeout = setTimeout(() => {
    console.log("🔁 Tentative de reconnexion WebSocket...");
    this.connectWebSocket();
  }, this.reconnectDelay);
}




}
