import { Component, OnInit, AfterViewInit } from '@angular/core';
import { EnDeviceService } from 'app/services/en-device.service';
import {ActivatedRoute} from '@angular/router';
import { endDevice } from 'app/models/endDevice';
import { NotificationsService } from 'app/services/notifications.service';
import { FarmService } from 'app/services/farm.service';
import { Farm } from 'app/models/farm';
import { ModifDevice } from 'app/models/modifDevice';
import { AuthService } from 'app/services/auth.service';
declare const google: any;

@Component({
  selector: 'app-modif-device',
  templateUrl: './modif-device.component.html',
  styleUrls: ['./modif-device.component.css']
})
export class ModifDeviceComponent {

  constructor(private devService : EnDeviceService,  private router : ActivatedRoute,
    private notifService : NotificationsService, private farmService : FarmService,
    private authService: AuthService
  ){}
  id : string
  readOnlyMode: boolean = true; // ou false selon le mode
  role : string ='';
  ngOnInit(){
    this.id = this.authService.CurrentUser.id;
    this.role = this.authService.CurrentUser.role;
    if(this.role == "Admin"){
      this.readOnlyMode = false;
    }
    this.devService.getDeviceById(this.router.snapshot.params['id']).subscribe((res : any) => {
      console.log("ress1", res)
      this.device = res;  
      this.GetFarms().then(() => {
      if (res.serre) {
      // Si une serre est définie, on récupère la ferme correspondante
      this.selectedFarm = this.ListFarms.find(f => f.id === res.serre.ferme.id);
      // On cherche aussi la bonne serre dans cette ferme
      this.device.serre = this.selectedFarm?.serres.find(s => s.id === res.serre.id) ?? null;
    } else {
      // Si aucune serre n’est définie, on met tout à null
      this.selectedFarm = null;
      this.device.serre = null;
    }
    });
    });
  }

  ngAfterViewInit(): void {
    this.initMap();
  }
    device : endDevice = { id : '', type:'', codDevice : '', nivBat: 254, sensors :[], localActuators : [], 
      config : {pa: 1, ps : 1 , lat: 36.8816580,longt :10.3166711},
    serre :  { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []}}
    microcontrollerTypes: string[] = ['STM32', 'LoRa_E5', 'ESP32', 'Arduino'];
    ListFarms : Farm[] = []
    selectedFarm: Farm | null = null;
    ModifDevice : ModifDevice = { type : '', config : {pa: 1, ps : 1 , lat: 36.8816580,longt :10.3166711}, serre : {id : 0},
   codDevice:'', sensors: [], localActuators: [], nivBat : 254}

  GetFarms(): Promise<void> {
  return new Promise((resolve, reject) => {
    this.farmService.getAllFarms(this.id).subscribe({
      next: (data) => {
        this.ListFarms = data;
        resolve();
      },
      error: (err) => {
        console.error('Erreur de récupération des fermes', err);
        reject(err);
      }
    });
  });
}


  onFarmChange() {
  this.device.serre = null; // Reset serre selection
  }

  onSubmit(){
    console.log("device", this.device)
    this.ModifDevice.type = this.device.type; this.ModifDevice.config = this.device.config; this.ModifDevice.nivBat = this.device.nivBat;
    this.ModifDevice.codDevice = this.device.codDevice;
    if(this.device.serre != null){
      this.ModifDevice.serre.id = Number(this.device.serre.id);
    }else{
      this.ModifDevice.serre = null;
    }
    // Pour les capteurs (sensors)
    if (this.device.sensors && Array.isArray(this.device.sensors)) {
      this.ModifDevice.sensors = this.device.sensors.map((sensor: any) => ({ id: Number(sensor.id) }));
    } else {
      this.ModifDevice.sensors = [];
    }

    // Pour les actionneurs locaux (localActuators)
    if (this.device.localActuators && Array.isArray(this.device.localActuators)) {
      this.ModifDevice.localActuators = this.device.localActuators.map((act: any) => ({ id: Number(act.id) }));
    } else {
      this.ModifDevice.localActuators = [];
    }

    console.log("À envoyer :", this.ModifDevice);
    this.devService.editDevice(this.ModifDevice, Number(this.device.id)).subscribe(res =>{
      console.log("ress22", res)
      this.devService.getDeviceById(this.device.id);
      this.notifService.showNotification('top', 'left', "L'appareil a été modifié avec succès",2);
    })
  }

    initMap(): void {
      const myLatlng = { lat: this.device.config.lat, lng: this.device.config.longt };
  
      const map = new google.maps.Map(document.getElementById("device-map"), {
        zoom: 13,
        center: myLatlng,
        scrollwheel: false,
      });
  
      let marker = new google.maps.Marker({
        position: myLatlng,
        map,
        title: "Position sélectionnée",
        draggable: true,
      });
  
      // Lors du clic sur la carte
      map.addListener("click", (event: any) => {
       this.device.config.lat = event.latLng.lat();
        this.device.config.longt = event.latLng.lng();
  
        marker.setPosition(event.latLng);
      });
  
      // Drag du marqueur
      marker.addListener("dragend", (event: any) => {
        this.device.config.lat = event.latLng.lat();
        this.device.config.longt = event.latLng.lng();
      });
    }

  compareDevices(dev1: any, dev2: any): boolean {
    return dev1 && dev2 ? dev1.id === dev2.id : dev1 === dev2;
  }
}
