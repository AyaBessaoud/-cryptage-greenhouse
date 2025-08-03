import { AfterViewInit, Component, OnInit } from '@angular/core';
import { addDevice } from 'app/models/addDevice';
import { EnDeviceService } from 'app/services/en-device.service';
import { NotificationsService } from 'app/services/notifications.service';
import { FarmService } from 'app/services/farm.service';
import { Farm } from 'app/models/farm';
import { AuthService } from 'app/services/auth.service';
declare const google: any;

@Component({
  selector: 'app-add-device',
  templateUrl: './add-device.component.html',
  styleUrls: ['./add-device.component.css']
})
export class AddDeviceComponent {

  constructor( public services : EnDeviceService,private authService: AuthService,
    private notificationService: NotificationsService, private farmService : FarmService){}

  // Liste des capteurs disponibles
  availableSensors = [
    { id: 'Capteur bme680', name: 'bme680', checked: false },
    { id: 'Capteur de lumière', name: 'lightIntensity', checked: false },
    { id: 'Capteur d humidité du sol', name: 'soilMoisture', checked: false }
  ];
  device : addDevice = { type : '', config : {pa: 1, ps : 1 , lat: 36.8816580,longt :10.3166711}, serre : {id : 0}}
  ListFarms : Farm[] = []
  selectedFarm: Farm | null = null;
  id : string;
  

  ngAfterViewInit(): void {
    this.initMap();
  }

  ngOnInit(){
    this.id = this.authService.CurrentUser.id;
    this.GetFarms()
  }

  GetFarms(){
    this.farmService.getAllFarms(this.id).subscribe(res=>{
      this.ListFarms = res;
    })
  }

  onSerreChange(serreId: string) {
    this.device.serre.id = parseInt(serreId, 10) || 0;
  }

  onFarmChange() {
  this.device.serre.id = 0; // Reset serre selection
  }

  getSensorIcon(sensorId: string): string {
    const icons = {
      'Capteur bme680': 'device_thermostat',
      'Capteur de lumière': 'wb_sunny',
      'Capteur d humidité du sol': 'opacity'
    };
    return icons[sensorId] || 'sensors';
  }
  microcontrollerTypes: string[] = ['STM32', 'LoRa_E5', 'ESP32', 'Arduino'];
  
  onSubmit() {

    console.log('devicceee',this.device);
    this.services.addDevice(this.device).subscribe(res=>{
      console.log('ress',res);
      this.device.type = ''; this.device.serre.id = 0;
      this.device.config = {ps: 1 , pa : 1 , lat :36.8816580 , longt :10.3166711 }
      this.notificationService.showNotification('top', 'left', "Le nouvel appareil a été ajouté avec succès",2);
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


}
