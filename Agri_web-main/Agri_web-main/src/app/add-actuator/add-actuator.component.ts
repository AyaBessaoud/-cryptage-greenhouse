import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { EnDeviceService } from 'app/services/en-device.service';
import { NotificationsService } from 'app/services/notifications.service';
import { ActuatorService } from 'app/services/actuator.service';
import { endDevice } from 'app/models/endDevice';
import { addActuator } from 'app/models/addActuator';
import { FarmService } from 'app/services/farm.service';
import { Farm } from 'app/models/farm';
import { AuthService } from 'app/services/auth.service';
import { GreenHouse } from 'app/models/GreenHouse';
declare const google: any;

@Component({
  selector: 'app-add-actuator',
  templateUrl: './add-actuator.component.html',
  styleUrls: ['./add-actuator.component.css']
})
export class AddActuatorComponent {

  constructor(private http: HttpClient, private actService: ActuatorService,
    private notifService: NotificationsService, private deviceServices: EnDeviceService,
  private farmService : FarmService, private authService: AuthService) { }

  ngAfterViewInit(): void {
    this.id = this.authService.CurrentUser.id;
    this.GetFarms();
    this.initMap();
    //this.getDevices();
  }

  initialact : addActuator =  { description : '', index :0, output : '', longt : 10.3166711 , lat : 36.8816580 , 
    sensors: [], device : {id : 0, codDevice: ''}}
    id : string;
  devList: endDevice[] = [];
  ListFarms : Farm[] = []
    selectedFarm: Farm | null = null;
    selectedSerre: GreenHouse | null = null;

  getDevices() {
    this.deviceServices.getAllDevices().subscribe(res => {
      this.devList = res;
      console.log(this.devList)
    }, err => {
      console.log("error while fetching data.")
    });
  }

  GetFarms(){
    this.farmService.getAllFarms(this.id).subscribe(res=>{
      this.ListFarms = res;
      console.log("res farm", res)
    })
  }

  changeFarm(){
    this.selectedFarm = null
  }

  onSubmit(){
    console.log("actionneur",this.act)
    this.actService.addActuator(this.act).subscribe(res=>{
      this.act = { ...this.initialact };
      this.notifService.showNotification('top', 'left', "Un nouvel actionneur a été ajouté avec succès",2);
      setTimeout(() => {
        window.location.reload();
      }, 2000);
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

  getSensorName(name: string): string {
    const type = {
      'gaz': 'Capteur de gaz',
      'lumiere': 'Capteur de lumière',
      'humSol': 'Capteur d\'humidité du sol',
      'humidite' : 'Capteur d\'humidité',
      'temperature' : 'Capteur de température'
    };
    return type[name]; // icône par défaut au cas où
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
  

  getActuatorIcon(sensorId: string): string {
    const icons = {
      'Lampe': 'bi-lightbulb-fill',
      'Ventilateur': 'bi-fan',
      'Pompe': 'bi-water',
    };
    return `bi ${icons[sensorId] || 'bi-question-circle'}`; // icône par défaut au cas où
  }
  

  // Liste des actionneur disponibles
availableActuator = [
  { name: 'Lampe', checked: false },
  { name: 'Ventilateur', checked: false },
  { name: 'Pompe', checked: false }
];

  initMap(): void {
    const myLatlng = { lat: this.act.lat, lng: this.act.longt };

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
      this.act.lat = event.latLng.lat();
      this.act.longt = event.latLng.lng();

      marker.setPosition(event.latLng);
    });

    // Drag du marqueur
    marker.addListener("dragend", (event: any) => {
      this.act.lat = event.latLng.lat();
      this.act.longt = event.latLng.lng();
    });
  }

  selectedActs: string[] = [];
  act: addActuator = { ...this.initialact };  //permet de faire une copie de initialact et l'assigner à act
  //Sélectionner/désélectionner un actionneur et remplir les infos
  toggleActuator(name: string) {
    const alreadySelected = this.selectedActs[0] === name;
  
    if (alreadySelected) {
      // Désélectionner
      this.selectedActs = [];
      this.act = { ...this.initialact };
    } else {
      // Sélectionner un nouveau capteur
      this.selectedActs = [name];
      this.act = {
        ...this.initialact,
        output: name
      };
    }
  
     console.log(this.act);
  }
  //Savoir si un capteur est sélectionné pour l'afficher différemment
  isSelected(name: string): boolean {
    return this.selectedActs.includes(name);
  }

    changeSerre(){
  console.log("change serre", this.act)
  this.act.device = null;
  this.selectedDevice = null;
}


  selectedSensors: number[] = []; // contient les IDs des capteurs sélectionnés
  // Représente le device sélectionné complet (avec ses capteurs)
  selectedDevice: endDevice | null = null;

  SelectDevice(dev: endDevice | null) {
    if (dev) {
      this.selectedDevice = dev;
      this.act.device = { id: Number(dev.id), codDevice: dev.codDevice }; // seulement les infos nécessaires
      this.selectedSensors = [];
      this.act.sensors = [];
    } else {
      this.selectedDevice = null;
      this.act.device = null;
    }
    console.log("device choisi", this.act.device);
  }
  
  compareDevices = (d1: any, d2: any): boolean => {
    return d1 && d2 ? Number(d1.id) === Number(d2.id) : d1 === d2;
  };  
  

// gestion du clic sur un capteur
toggleSensor(sensorId: number) {
  const index = this.selectedSensors.indexOf(sensorId);
  if (index > -1) {
    // déselection
    this.selectedSensors.splice(index, 1);
  } else {
    // sélection
    this.selectedSensors.push(sensorId);
  }

  // Mise à jour de act.sensors sous forme [{id: string}]
  this.act.sensors = this.selectedSensors.map(id => ({ id }));
  console.log("capteur choisi", this.act)
}

// utilitaire pour vérifier si un capteur est sélectionné
isSelected2(sensorId: number): boolean {
  return this.selectedSensors.includes(sensorId);
}

}
