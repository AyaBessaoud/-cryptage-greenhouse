import { Component } from '@angular/core';
import { EnDeviceService } from 'app/services/en-device.service';
import { ActivatedRoute } from '@angular/router';
import { endDevice } from 'app/models/endDevice';
import { AuthService } from 'app/services/auth.service';
import { FarmService } from 'app/services/farm.service';
import { Farm } from 'app/models/farm';
declare const google: any;

@Component({
  selector: 'app-view-device',
  templateUrl: './view-device.component.html',
  styleUrls: ['./view-device.component.css']
})
export class ViewDeviceComponent {

  constructor( private devService : EnDeviceService, private router : ActivatedRoute,
    private farmService : FarmService,private authService: AuthService
  ){}

  ngOnInit(){
    this.id = this.authService.CurrentUser.id;
    this.devService.getDeviceById(this.router.snapshot.params['id']).subscribe((res : any) => {
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
      this.initMap();
    });
  }

  ngAfterViewInit(): void {
    //this.initMap();
  }
    device : endDevice = { id : '',type:'', codDevice : '', nivBat: 254, sensors :[], localActuators : [],
      serre:  { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []}, 
      config : {pa: 1, ps : 1 , lat: 36.8816580,longt :10.3166711}}
    id : string
    ListFarms : Farm[] = []
    selectedFarm: Farm | null = null;
    readOnlyMode: boolean = true; // ou false selon le mode

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
    compareDevices(dev1: any, dev2: any): boolean {
    return dev1 && dev2 ? dev1.id === dev2.id : dev1 === dev2;
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

getActuatorIcon(act: string): string {
  // Ajoute d'autres icônes selon tes actionneurs
  const icons = {
    'Lampe': 'bi-lightbulb-fill',
    'Ventilateur': 'bi-fan',
    'Pompe': 'bi-water',
  };
  return `bi ${icons[act] || 'bi-question-circle'}`; // icône par défaut au cas où
}

getSensorIcon(sensorId: string): string {
  const icons = {
    'temperature': 'device_thermostat',
    'lumiere': 'wb_sunny',
    'humSol': 'opacity',
    'gaz' : 'co2',
    'humidite': "opacity"
  };
  return icons[sensorId] || 'sensors';
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
