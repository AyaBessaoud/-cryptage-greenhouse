import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NotificationsService } from 'app/services/notifications.service';
import { SensorService } from 'app/services/sensor.service';
import { Sensor } from 'app/models/Sensor';
import { EnDeviceService } from 'app/services/en-device.service';
import { ModifSensor } from 'app/models/modifSensor';
import { FarmService } from 'app/services/farm.service';
import { Farm } from 'app/models/farm';
import { AuthService } from 'app/services/auth.service';
import { GreenHouse } from 'app/models/GreenHouse';
declare const google: any;

@Component({
  selector: 'app-modif-sensor',
  templateUrl: './modif-sensor.component.html',
  styleUrls: ['./modif-sensor.component.css']
})
export class ModifSensorComponent {

  constructor(private sensorService: SensorService, private router: ActivatedRoute,
    private notifService: NotificationsService, private deviceServices: EnDeviceService,
  private authService: AuthService, private farmService : FarmService) { }

  id : string;
  readOnlyMode: boolean = true; // ou false selon le mode
  role : string ='';
  ListFarms : Farm[] = []
  selectedFarm: Farm | null = null;
  selectedSerre: GreenHouse | null = null;

  ngOnInit() {
    this.id = this.authService.CurrentUser.id;
    console.log("id" , this.id)
    this.role = this.authService.CurrentUser.role;
    if(this.role == "Admin"){
      this.readOnlyMode = false;
    }
    this.sensorService.getSensorById(this.router.snapshot.params['id']).subscribe((res: any) => {
      this.sensor = res;
      this.sensor.endDevice = res.device;
      console.log("res" , res)
      console.log(this.sensor)
      const selectedType = this.sensor.typeSensor;

      if (selectedType) {
        this.selectedSensors = [selectedType];

        this.availableSensors.forEach(sensor => {
          sensor.checked = sensor.name === selectedType;
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
    });
    this.getDevices();
    this.initMap();
  }

  //appeler cette fonction pour chaque élément dans devList, pour le comparer à sensor.endDevice
  compareDevices(dev1: any, dev2: any): boolean {
    return dev1 && dev2 ? Number(dev1.id) === Number(dev2.id) : dev1 === dev2;
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

verifDevice(){
  console.log("sennnssor", this.sensor)
}
changeSerre(){
  console.log("change serre", this.sensor)
  this.sensor.endDevice = null;
}

  onSubmit() {
  this.modifSensor.idU = this.sensor.idU; this.modifSensor.index= this.sensor.index;
    this.modifSensor.description = this.sensor.description; this.modifSensor.alertThersholdD = this.sensor.alertThersholdD;
    this.modifSensor.alertThersholdN = this.sensor.alertThersholdN; this.modifSensor.normalThersholdD = this.sensor.normalThersholdD;
    this.modifSensor.normalThersholdN = this.sensor.normalThersholdN; this.modifSensor.longt = this.sensor.longt; this.modifSensor.lat= this.sensor.lat;
    this.modifSensor.typeSensor = this.sensor.typeSensor; 
    if(this.sensor.endDevice != null){
      this.modifSensor.endDevice.id = Number(this.sensor.endDevice.id);
    }else{
      this.modifSensor.endDevice = null;
    }
    
    this.modifSensor.actuators = this.sensor.actuators.map(act => ({ id: Number(act.id) }));
    console.log("sensor to sennndd",this.modifSensor)
    this.sensorService.editSensor(this.modifSensor, this.sensor.id).subscribe(res => {
      console.log("final res", res)
      this.sensorService.getSensorById(this.sensor.id)
      this.notifService.showNotification('top', 'left', "Le capteur a été modifié avec succès", 2);
    })
  }

  modifSensor : ModifSensor = { idU : '', index: 0,description: '',alertThersholdD: 0,normalThersholdD: 0,actuators: [],
    typeSensor: '',lat: 36.8816580,longt: 10.3166711,normalThersholdN : 0,alertThersholdN : 0,endDevice: {id: 0}}
  selectedSensors: string[] = [];
  sensor: Sensor = {idU:'',alertThersholdD: 0, normalThersholdD: 0,
    index: 0, id: '', description: '', alertThersholdN: 0, normalThersholdN: 0, actuators: [], typeSensor: '',
    lat: 36.8816580, longt: 10.3166711, endDevice: { type :'', serre :  { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []},
      id: '', codDevice: '', nivBat: 254, sensors: [],
      localActuators: [], config: { pa: 1, ps: 1, lat: 36.8816580, longt: 10.3166711 }
    }
  }

  //Sélectionner/désélectionner un capteur et remplir les infos
  toggleSensor(sensorId: string) {
    const alreadySelected = this.selectedSensors[0] === sensorId;

    if (alreadySelected) {
      // Désélectionner
      this.selectedSensors = [];
      this.sensor.typeSensor = "";
    } else {
      // Sélectionner un nouveau capteur
      this.selectedSensors = [sensorId];
      this.sensor.typeSensor = sensorId;
    }

    console.log(this.sensor);
  }
  //Savoir si un capteur est sélectionné pour l'afficher différemment
  isSelected(sensorId: string): boolean {
    return this.selectedSensors.includes(sensorId);
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
