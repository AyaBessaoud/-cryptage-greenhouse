import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { EnDeviceService } from 'app/services/en-device.service';
import { NotificationsService } from 'app/services/notifications.service';
import { ActuatorService } from 'app/services/actuator.service';
import { endDevice } from 'app/models/endDevice';
import { modifActuator } from 'app/models/modifActuator';
import { Actuator } from 'app/models/Actuator';
import { FarmService } from 'app/services/farm.service';
import { Farm } from 'app/models/farm';
import { AuthService } from 'app/services/auth.service';
import { GreenHouse } from 'app/models/GreenHouse';
declare const google: any;

@Component({
  selector: 'app-modif-actuator',
  templateUrl: './modif-actuator.component.html',
  styleUrls: ['./modif-actuator.component.css']
})
export class ModifActuatorComponent {

  constructor(private http: HttpClient, private actService: ActuatorService,private router: ActivatedRoute,
    private notifService: NotificationsService, private deviceServices: EnDeviceService,
  private authService: AuthService, private farmService : FarmService) { }

  ngAfterViewInit(): void {
    this.id = this.authService.CurrentUser.id;
    this.actService.getById(this.router.snapshot.params['id']).subscribe(res =>{
        this.act = {
          ...this.act,
          id: res.id,
          index: res.index,
          description: res.description,
          longt: res.longt,
          lat: res.lat,
          output: res.output,
          idU: res.idU,
          sensors: res.sensors || [] // les capteurs liés à cet actionneur
        };
        if(res.device){
          this.act.device = res.device;
        }
        this.selectedSensorIds = this.act.sensors.map(s => s.id);
      console.log("actionnnneeur",this.act)

      const selectedType = this.act.output;
      if (selectedType) {
        this.selectedActs = [selectedType];

        this.availableActuator.forEach(actuator => {
          actuator.checked = actuator.name === selectedType;
        });
      }

      this.GetFarms().then(() => {
      if (res.device) {
      // Si une serre est définie, on récupère la ferme correspondante
      this.selectedFarm = this.ListFarms.find(f => f.id === res.device.serre.ferme.id);
      // On cherche aussi la bonne serre dans cette ferme
      this.selectedSerre = this.selectedFarm?.serres.find(s => s.id === res.device.serre.id) ?? null;
    } else {
      // Si aucune serre n’est définie, on met tout à null
      this.selectedFarm = null;
      this.selectedSerre = null;
    }
    });
      
    })
    this.initMap();
    this.getDevices();
    
  }
  id : string;
  ListFarms : Farm[] = []
  selectedFarm: Farm | null = null;
  selectedSerre: GreenHouse | null = null;
  act : Actuator =  {idU: '', description : '',id : '', index :0, output : '', longt : 10.3166711 , lat : 36.8816580 ,
      sensors: [], device : {id : '',type:'', codDevice : '', nivBat: 254, sensors :[], localActuators : [], 
        config : {pa: 1, ps : 1 , lat: 36.8816580,longt :10.3166711}, 
      serre :  { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []}}}

  ActToSend : modifActuator = {idU : '', description : '',id : '', index :0, output : '', longt : 10.3166711 , lat : 36.8816580 ,
    sensors: [], device : {id : '', config : {pa: 1, ps : 1 , lat: 36.8816580,longt :10.3166711}}}

  selectedSensorIds: String[] = [];

  devList: endDevice[] = [];
  getDevices() {
    this.deviceServices.getAllDevices().subscribe(res => {
      this.devList = res;
      console.log(this.devList)
    }, err => {
      console.log("error while fetching data.")
    });
  }

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

  onSubmit(){
    this.ActToSend.description = this.act.description; this.ActToSend.index = this.act.index;
    this.ActToSend.id = this.act.id; this.ActToSend.output = this.act.output;
    this.ActToSend.longt = this.act.longt; this.ActToSend.lat = this.act.lat; this.ActToSend.idU = this.act.idU
    this.ActToSend.sensors = this.selectedSensorIds.map(id => ({ id }));
    if(this.act.device.id != ''){
      this.ActToSend.device.id = this.act.device.id;
       this.ActToSend.device.config = this.act.device.config;
    }
    else {
      this.ActToSend.device = null;
    }
    
    console.log("actuu", this.ActToSend)
    this.actService.editActuator(this.ActToSend).subscribe(res =>{
      console.log("ress", res)
      this.actService.getById(this.act.id);
      this.notifService.showNotification('top', 'left', "l\'actionneur a été modifié avec succès",2);
      //this.notifService.addNotification("test")
    })
  }
  emptyDevice = {
    id: '',
    codDevice: '',
    nivBat: 254,
    sensors: [],
    localActuators: [],
    config: {
      pa: 1,
      ps: 1,
      lat: 36.8816580,
      longt: 10.3166711
    }
  };
  
  emptySensor  = {index: 0, id : '', description: '', fctMode: 0, alertThershold: 0, normalThershold: 0, actuators: [], typeSensor: '',
    lat: 36.8816580, longt: 10.3166711, endDevice: { id: '', codDevice: '', nivBat: 254, sensors: [],
      localActuators: [], config: { pa: 1, ps: 1, lat: 36.8816580, longt: 10.3166711}
    }}

  compareDevices(dev1: any, dev2: any): boolean {
    if (!dev1 && !dev2) return true;
    if (!dev1 || !dev2) return false;
  
    // Comparaison spéciale si id vide
    if (dev1.id === '' && dev2.id === '') return true;
  
    return dev1.id === dev2.id;
  }
  
  

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
  isSelected2(sensorId: string): boolean {
    return this.selectedSensorIds.includes(sensorId);
  }
  
  toggleSensor(sensorId: string) {
    const index = this.selectedSensorIds.indexOf(sensorId);
    if (index > -1) {
      this.selectedSensorIds.splice(index, 1);
    } else {
      this.selectedSensorIds.push(sensorId);
    }
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

  changeSerre(){
  console.log("change serre",  this.act)
   this.act.device = null;
}
  
  selectDev(){
    this.act.sensors = []
    this.selectedSensorIds = []
    console.log("veriif", this.act)
  }

  // Liste des actionneur disponibles
availableActuator = [
  { name: 'Lampe', checked: false },
  { name: 'Ventilateur', checked: false },
  { name: 'Pompe', checked: false }
];

selectedActs: String[] = [];
  //Sélectionner/désélectionner un actionneur et remplir les infos
  toggleActuator(name: string) {
    const alreadySelected = this.selectedActs[0] === name;
  
    if (alreadySelected) {
      // Désélectionner
      this.selectedActs = [];
      this.act.output = '';
    } else {
      // Sélectionner un nouveau capteur
      this.selectedActs = [name];
      this.act.output = name;
    }
  
     console.log(this.act);
  }
  //Savoir si un capteur est sélectionné pour l'afficher différemment
  isSelected(name: string): boolean {
    return this.selectedActs.includes(name);
  }

}
