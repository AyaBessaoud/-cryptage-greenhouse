import { Component } from '@angular/core';
import { SensorService } from 'app/services/sensor.service';
import { Sensor } from 'app/models/Sensor';
import { EnDeviceService } from 'app/services/en-device.service';
import { ActivatedRoute } from '@angular/router';
import { FarmService } from 'app/services/farm.service';
import { Farm } from 'app/models/farm';
import { AuthService } from 'app/services/auth.service';
import { GreenHouse } from 'app/models/GreenHouse';
declare const google: any;

@Component({
  selector: 'app-view-sensor',
  templateUrl: './view-sensor.component.html',
  styleUrls: ['./view-sensor.component.css']
})
export class ViewSensorComponent {

  constructor(private sensorService: SensorService, private router: ActivatedRoute,
     private deviceServices: EnDeviceService, private authService: AuthService, private farmService : FarmService) { }

     ngOnInit() {
      this.sensorService.getSensorById(this.router.snapshot.params['id']).subscribe((res: any) => {
        this.sensor = res;
        console.log("ress",this.sensor)
        const selectedType = this.sensor.typeSensor;
  
        if (selectedType) {
          this.selectedSensors = [selectedType];
  
          this.availableSensors.forEach(sensor => {
            sensor.checked = sensor.name === selectedType;
          });
        }
      });
      this.getDevices();
      this.initMap();
    }

    readOnlyMode: boolean = true; // ou false selon le mode

    //appeler cette fonction pour chaque élément dans devList, pour le comparer à sensor.endDevice
      compareDevices(dev1: any, dev2: any): boolean {
        return dev1 && dev2 ? dev1.id === dev2.id : dev1 === dev2;
      }

    
      selectedSensors: string[] = [];
      sensor: Sensor = {idU:'',normalThersholdN:0, 
        index: 0, id: '', description: '', alertThersholdD: 0, normalThersholdD: 0, actuators: [], typeSensor: '',alertThersholdN :0,
        lat: 36.8816580, longt: 10.3166711, endDevice: {type:'', serre: { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []},
          id: '', codDevice: '', nivBat: 254, sensors: [],
          localActuators: [], config: { pa: 1, ps: 1, lat: 36.8816580, longt: 10.3166711 }
        }
      }
       
      devList = [];
      getDevices() {
        this.deviceServices.getAllDevices().subscribe(res => {
          this.devList = res;
          console.log(this.devList)
        }, err => {
          console.log("error while fetching data.")
        });
      }
    
      getSensorIcon(sensorId: string): string {
        const icons = {
          'Capteur de température': 'device_thermostat',
          'Capteur de lumière': 'wb_sunny',
          'Capteur d\'humidité du sol': 'opacity',
          'Capteur de gaz': 'co2',
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

      //Savoir si un capteur est sélectionné pour l'afficher différemment
  isSelected(sensorId: string): boolean {
    return this.selectedSensors.includes(sensorId);
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
