import { Component, AfterViewInit } from '@angular/core';
import { AddSensor } from 'app/models/AddSensor';
import { HttpClient } from '@angular/common/http';
import { NotificationsService } from 'app/services/notifications.service';
import { SensorService } from 'app/services/sensor.service';
import { EnDeviceService } from 'app/services/en-device.service';
import { endDevice } from 'app/models/endDevice';
import { FarmService } from 'app/services/farm.service';
import { Farm } from 'app/models/farm';
import { AuthService } from 'app/services/auth.service';
import { GreenHouse } from 'app/models/GreenHouse';
declare const google: any;

@Component({
  selector: 'app-add-sensor',
  templateUrl: './add-sensor.component.html',
  styleUrls: ['./add-sensor.component.css']
})
export class AddSensorComponent {

  constructor(private http : HttpClient, private sensorService : SensorService,
    private notifService: NotificationsService, public deviceServices : EnDeviceService,
    private farmService : FarmService, private authService: AuthService) {}

    ngAfterViewInit(): void {
      this.initMap();
    }

    ngOnInit(){
    this.id = this.authService.CurrentUser.id;
    this.GetFarms();
    //this.getDevices();
  }

    id : string;
    devList = [];
    ListFarms : Farm[] = []
    selectedFarm: Farm | null = null;
    selectedSerre: GreenHouse | null = null;

    getDevices(){
      this.deviceServices.getAllDevices().subscribe(res=>{
        this.devList = res;
        console.log(this.devList)
    },err=>{
      console.log("error while fetching data.")
    });
    }

    GetFarms(){
    this.farmService.getAllFarms(this.id).subscribe(res=>{
      this.ListFarms = res;
      console.log("res farm", res)
    })
  }

    getSensorIcon(sensorId: string): string {
      const icons = {
        'Capteur de température': 'device_thermostat',
        'Capteur de lumière': 'wb_sunny',
        'Capteur d\'humidité du sol': 'opacity',
        'Capteur de gaz' : 'co2',
        'Capteur d\'humidité': "opacity"
      };
      return icons[sensorId] || 'sensors';
    }

    // Liste des capteurs disponibles
  availableSensors = [
    { id: 'Capteur de gaz', name: 'gaz', checked: false },
    { id: 'Capteur de lumière', name: 'lumiere', checked: false },
    { id: 'Capteur d\'humidité du sol', name: 'humSol', checked: false },
    { id: 'Capteur d\'humidité', name: 'humidite', checked: false },
    { id: 'Capteur de température', name: 'temperature', checked: false },
  ];

  selectedSensors: string[] = [];

// Structure initiale du capteur sélectionné
initialSensor: AddSensor = {
  index: 0,
  description: '',
  alertThersholdD: 0,
  normalThersholdD: 0,
  actuators: [],
  typeSensor: '',
  lat: 36.8816580,
  longt: 10.3166711,
  normalThersholdN : 0,
  alertThersholdN : 0,
  endDevice: {
    id: 0
  }
};

onSubmit(){
  console.log("sensorr", this.sensor)
  this.sensorService.addSensor(this.sensor).subscribe(res=>{
    this.sensor = { ...this.initialSensor };
    console.log("ress", res)
    this.notifService.showNotification('top', 'left', "Un nouveau capteur a été ajouté avec succès",2);
  //  setTimeout(() => {
 // window.location.reload();
//}, 2000);
    console.log("res",res)
  })
}


sensor: AddSensor = { ...this.initialSensor };  //permet de faire une copie de initialSensor et l'assigner à sensor
//Sélectionner/désélectionner un capteur et remplir les infos
toggleSensor(sensorId: string) {
  const alreadySelected = this.selectedSensors[0] === sensorId;

  if (alreadySelected) {
    // Désélectionner
    this.selectedSensors = [];
    this.sensor = { ...this.initialSensor };
  } else {
    // Sélectionner un nouveau capteur
    this.selectedSensors = [sensorId];
    this.sensor = {
      ...this.initialSensor,
      typeSensor: sensorId
    };
  }

   console.log(this.sensor);
}
//Savoir si un capteur est sélectionné pour l'afficher différemment
isSelected(sensorId: string): boolean {
  return this.selectedSensors.includes(sensorId);
}

selectedDevice: endDevice | null = null;
SelectDevice(dev: endDevice | null) {
    if (dev) {
      this.sensor.endDevice = { id: Number(dev.id) }; // seulement les infos nécessaires
    } else {
      this.sensor.endDevice = null;
    }
    console.log("device choisi", this.sensor.endDevice);
  }

  verifSerre(){
    console.log("verif serre", this.selectedSerre)
    console.log("verif farm", this.selectedFarm)
  }
  changeSerre(){
  console.log("change serre", this.sensor)
  this.selectedDevice = null;
}

initMap(): void {
  const myLatlng = { lat: this.sensor.lat, lng: this.sensor.longt };

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
   this.sensor.lat = event.latLng.lat();
    this.sensor.longt = event.latLng.lng();

    marker.setPosition(event.latLng);
  });

  // Drag du marqueur
  marker.addListener("dragend", (event: any) => {
    this.sensor.lat = event.latLng.lat();
    this.sensor.longt = event.latLng.lng();
  });
}
  

}
