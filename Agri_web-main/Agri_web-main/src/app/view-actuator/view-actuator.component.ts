import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EnDeviceService } from 'app/services/en-device.service';
import { ActuatorService } from 'app/services/actuator.service';
import { endDevice } from 'app/models/endDevice';
import { Actuator } from 'app/models/Actuator';
declare const google: any;

@Component({
  selector: 'app-view-actuator',
  templateUrl: './view-actuator.component.html',
  styleUrls: ['./view-actuator.component.css']
})
export class ViewActuatorComponent {

  constructor(private actService: ActuatorService, private router: ActivatedRoute,
    private deviceServices: EnDeviceService) { }

  ngAfterViewInit(): void {
    this.actService.getById(this.router.snapshot.params['id']).subscribe(res => {
      /*this.act.description = res.description;
      this.act.id = res.id;
      if (res.device) {
        this.act.device = res.device;
      }
      this.act.index = res.index;
      this.act.lat = res.lat;
      this.act.longt = res.longt;
      this.act.output = res.output;
      this.act.fctMode = res.fctMode;
      if (res.localSensor) {
        this.act.localSensor.id = res.localSensor.id;
      }*/
     this.act = res;

      console.log("actionnnneeur", this.act)

      const selectedType = this.act.output;
      if (selectedType) {
        this.selectedActs = [selectedType];

        this.availableActuator.forEach(actuator => {
          actuator.checked = actuator.name === selectedType;
        });
      }

    })
    this.initMap();
    this.getDevices();

  }

  readOnlyMode: boolean = true; // ou false selon le mode


  act: Actuator = {idU: '',
    description: '', id: '', index: 0, output: '', longt: 10.3166711, lat: 36.8816580,
    sensors: [], device: { id: '',type:'', codDevice: '', nivBat: 254, sensors: [], localActuators: [], 
      config: { pa: 1, ps: 1, lat: 36.8816580, longt: 10.3166711 },
    serre:  { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []} }
  }
  devList: endDevice[] = [];
  getDevices() {
    this.deviceServices.getAllDevices().subscribe(res => {
      this.devList = res;
      console.log(this.devList)
    }, err => {
      console.log("error while fetching data.")
    });
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
    
  
    // Liste des actionneur disponibles
  availableActuator = [
    { name: 'Lampe', checked: false },
    { name: 'Ventilateur', checked: false },
    { name: 'Pompe', checked: false }
  ];
  
  selectedActs: String[] = [];
    //Savoir si un capteur est sélectionné pour l'afficher différemment
    isSelected(name: string): boolean {
      return this.selectedActs.includes(name);
    }
  
}
