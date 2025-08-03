import { Component } from '@angular/core';
import { LogsService } from 'app/services/logs.service';
import { GreenHouseService } from 'app/services/green-house.service';
import { AuthService } from 'app/services/auth.service';
import { SensorHistryService } from 'app/services/sensor-histry.service';
import { baseUrl } from 'environments/environment';
import { Client, IMessage, Stomp } from '@stomp/stompjs';  //servent à gérer la connexion et les messages STOMP.
import * as SockJS from 'sockjs-client';  //crée une connexion WebSocket
//import { ChartOptions, ChartType, ChartDataset } from 'chart.js';
import {
  ApexAxisChartSeries,
  ApexChart,
  ApexXAxis,
  ApexTitleSubtitle,
  ApexStroke,
  ApexDataLabels,
  ApexTooltip
} from 'ng-apexcharts';
export type ChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  xaxis: ApexXAxis;
  title: ApexTitleSubtitle;
  stroke: ApexStroke;
  dataLabels: ApexDataLabels;
  tooltip: ApexTooltip;
};


@Component({
  selector: 'app-dashboardd',
  templateUrl: './dashboardd.component.html',
  styleUrls: ['./dashboardd.component.css']
})
export class DashboarddComponent {

  constructor(private logService: LogsService,
    private authService: AuthService, private serreService: GreenHouseService,
    private sensorService: SensorHistryService
  ) { }

  ngOnInit() {
    this.id = this.authService.CurrentUser.id;
    this.GetAllGreenHouses();
    this.connectWebSocket()
  }

  selectedHistory: any;
  serresList = []
  id: string;
  currentSerreIndex: number = 0;
  codDevices: string[] = [];
  currentCodDeviceIndex: number = 0;
  selectedDeviceData: any = null;
  sensorHistoryData: any = null;


  //pour verifier si la liste retournée n'est pas vide
  hasHistoryData(): boolean {
    if (!this.selectedHistory || Object.keys(this.selectedHistory).length === 0) {
      return false;
    }

    // Vérifie si au moins un des appareils contient un historique avec des données utiles
    return this.codDevices.some(deviceCode => {
      const device = this.selectedHistory[deviceCode];
      return (
        device?.deviceHistory &&
        (device?.sensors && Object.keys(device.sensors).length > 0) &&
        (device?.actuators && Object.keys(device.actuators).length > 0)
      );
    });
  }

  getSensorArray(sensors: any): any[] {
    return sensors ? Object.values(sensors) : [];
  }

  getActuatorArray(actuators: any): any[] {
    return actuators ? Object.values(actuators) : [];
  }

  getBatteryIcon(nivBat: number): string {
    const percentage = (nivBat / 254) * 100;
    if (percentage == 100) return 'battery_full';
    else if (percentage >= 50) return 'battery_5_bar';
    else if (percentage >= 20) return 'battery_3_bar';
    else return 'battery_1_bar';
  }

  getSensorIcon(sensorId: string): string {
    const icons: { [key: string]: string } = {
      'temperature': 'device_thermostat',
      'lumiere': 'wb_sunny',
      'humSol': 'opacity',
      'gaz': 'co2',
      'humidite': 'opacity'
    };
    return icons[sensorId] || 'sensors';
  }

  getSensorType(type: string): string {
    const types = {
      'temperature': 'Température',
      'lumiere': 'Lumière',
      'humidite': 'Humidité',
      'gaz': 'Gaz',
      'humSol': "Humidité du sol"
    };
    return types[type];
  }

  getActuatorIcon(name: string): string {
    const icons: { [key: string]: string } = {
      'Lampe': 'bi bi-lightbulb-fill',
      'Ventilateur': 'bi bi-fan',
      'Pompe': 'bi bi-water'
    };
    return icons[name] || 'bi bi-question-circle';
  }

  loadHistoryBySerre(idSerre: string) {
    this.selectedDeviceData = null
    this.logService.getHistoryBySerre(idSerre).subscribe(data => {
      this.selectedHistory = data;
      this.codDevices = Object.keys(this.selectedHistory);
      this.currentCodDeviceIndex = 0;

      if (this.codDevices.length > 0) {
        this.selectedDeviceData = this.selectedHistory[this.codDevices[0]];
      }

      console.log("history data", this.selectedHistory);
      console.log("history device", this.selectedDeviceData);
      this.loadSensorDataIfAvailable();
    });
  }


  //pour recupération des serres disponibles
  GetAllGreenHouses() {
    this.serresList = []
    this.serreService.getAll(this.id).subscribe(res => {
      console.log("ress", res)
      this.serresList = res;
      this.currentSerreIndex = 0;
      if (this.serresList.length > 0) {
        this.loadHistoryBySerre(this.serresList[0].id);
      }
    })
  }

  //pour la gestion de navigation du device
  prevSerre() {
    console.log("prev", this.currentSerreIndex)
    if (this.currentSerreIndex > 0) {
      this.currentSerreIndex--;
      this.sensorHistoryData = null;
      this.loadHistoryBySerre(this.serresList[this.currentSerreIndex].id);
    }
  }

  nextSerre() {
    console.log("next", this.currentSerreIndex)
    if (this.currentSerreIndex < this.serresList.length - 1) {
      this.currentSerreIndex++;
      this.sensorHistoryData = null;
      this.loadHistoryBySerre(this.serresList[this.currentSerreIndex].id);
    }
  }

  isFirst(): boolean {
    return this.currentSerreIndex === 0;
  }

  isLast(): boolean {
    return this.currentSerreIndex === this.serresList.length - 1;
  }

  //pour naviguer entre les devices
  prevCodDevice() {
    if (this.currentCodDeviceIndex > 0) {
      this.currentCodDeviceIndex--;
      this.sensorHistoryData = null;
      this.selectedDeviceData = this.selectedHistory[this.codDevices[this.currentCodDeviceIndex]];
      this.loadSensorDataIfAvailable();
    }
  }

  nextCodDevice() {
    if (this.currentCodDeviceIndex < this.codDevices.length - 1) {
      this.currentCodDeviceIndex++;
      this.sensorHistoryData = null;
      this.selectedDeviceData = this.selectedHistory[this.codDevices[this.currentCodDeviceIndex]];
      this.loadSensorDataIfAvailable();
    }
  }

  getCurrentCodDevice(): string {
    return this.codDevices[this.currentCodDeviceIndex];
  }
  //code des chart
  loadSensorDataIfAvailable() {
    this.sensorHistoryData = null;
    const codDevice = this.getCurrentCodDevice();

    // Vérifie que le codDevice existe et contient des données dans selectedHistory
    if (codDevice && this.selectedHistory[codDevice] && this.selectedDeviceData.sensors.length > 0) {
      this.sensorService.getSensorHistory(codDevice).subscribe(
        data => {
          console.log("Données capteur reçues :", data);
          this.sensorHistoryData = data; // ou tout autre traitement que tu veux
          this.prepareIndividualCharts(this.sensorHistoryData);
        },
        error => {
          console.error(" Erreur lors de la récupération des données capteur :", error);
        }
      );
    } else {
      console.warn("Aucun historique disponible pour le codDevice :", codDevice);
      this.sensorHistoryData = null;
    }
  }

  // Données pour chaque capteur
  chartLabels: string[] = [];

  temperatureChart!: Partial<ChartOptions>;
  humiditeChart!: Partial<ChartOptions>;
  humiditeSolChart!: Partial<ChartOptions>;
  gazChart!: Partial<ChartOptions>;

  prepareIndividualCharts(data: any) {
    const temperature = data.temperature || [];
    const humidite = data.humidite || [];
    const humiditeSol = data.humSol || [];
    const gaz = data.gaz || [];

    this.chartLabels = temperature.map(entry => this.formatDate(entry.date));

    this.temperatureChart = this.buildChart('Température', temperature.map(e => e.value), '#aa3c78');
    this.humiditeChart = this.buildChart('Humidité', humidite.map(e => e.value), '#113e79');
    this.humiditeSolChart = this.buildChart('Humidité du sol', humiditeSol.map(e => e.value), '#1a3d09');
    this.gazChart = this.buildChart('Gaz', gaz.map(e => e.value), '#795104');
  }

  buildChart(title: string, data: number[], color: string): Partial<ChartOptions> {
    return {
      series: [
        {
          name: title,
          data: data
        }
      ],
      chart: {
        height: 300,
        type: 'line',
        zoom: {
          enabled: false
        }
      },
      xaxis: {
        categories: this.chartLabels
      },
      title: {
        text: title
      },
      stroke: {
        curve: 'smooth',
        colors: [color]
      },
      dataLabels: {
        enabled: false
      },
      tooltip: {
        enabled: true
      },
    };
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '';

    const date = new Date(dateStr);
    if (isNaN(date.getTime())) {
      console.warn("Date invalide :", dateStr);
      return '';
    }

    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  private stompClient: Client;  //gérer la connexion STOMP
  connectWebSocket() {
    const socket = new SockJS(baseUrl + '/notif');
    this.stompClient = Stomp.over(() => socket);

    this.stompClient.onConnect = () => {
      this.stompClient.subscribe('/topic/logs', message => {
        const obj = JSON.parse(message.body);
        this.handleLogUpdate(obj);
      });
    };

    this.stompClient.onStompError = (frame) => {
    console.error('Erreur STOMP :', frame.headers['message'], frame.body);
    };

    this.stompClient.activate();
  }

  handleLogUpdate(obj: any) {
    if (!obj.codDevice) return;

    const codDevice = obj.codDevice;
    const deviceData = this.selectedHistory[codDevice] || {};
    const historyArray = deviceData.deviceHistory || [];
    console.log("deviceData", deviceData)

    // On vérifie si un historique avec le même codDevice existe déjà
    const existingIndex = historyArray.findIndex(item => item.codDevice === obj.codDevice && 'battery' in obj);

    if (existingIndex !== -1) {
      console.log("device trouvé et remplacé")
      // On remplace l'entrée existante
      historyArray[existingIndex] = obj;
      // Mise à jour de l'objet dans la map
      this.selectedHistory[codDevice] = {
        ...(deviceData || {}),  //Si deviceData est défini (non null), utilise-le. Sinon, utilise un objet vide {}
        deviceHistory: historyArray  //on remplace la propriété deviceHistory de cet objet par obj
      };
    }

    // Si c'est un SensorHistory
    else if ('typeSensor' in obj && 'index' in obj && 'value' in obj) {
      let sensors = (deviceData?.sensors || []).slice();
      const idx = sensors.findIndex(s =>
        s.codDevice === obj.codDevice &&
        s.typeSensor === obj.typeSensor &&
        s.index === obj.index
      );

      if (idx !== -1) {
        console.log("sensor trouvé et remplacé")
        sensors[idx].date = obj.date;
        sensors[idx].value = obj.value;
        sensors[idx].etat = obj.etat;
      } 

      this.selectedHistory[codDevice] = {
        ...(deviceData || {}), 
        sensors
      };
    }

    // Si c'est un ActuatorHistoryQ
    else if ('output' in obj && 'index' in obj && 'etat' in obj) {
      let actuators = (deviceData?.actuators || []).slice();
      const idx = actuators.findIndex(a =>
        a.codDevice === obj.codDevice &&
        a.output === obj.output &&
        a.index === obj.index
      );

      if (idx !== -1) {
        console.log("actuator trouvé et remplacé")
        actuators[idx].date = obj.date;
        actuators[idx].etat = obj.etat;
      } 

      this.selectedHistory[codDevice] = {
        ...(deviceData || {}),
        actuators
      };
    }

    // Si on regarde actuellement ce device, on rafraîchit
    if (this.codDevices[this.currentCodDeviceIndex] === codDevice) {
      this.selectedDeviceData = this.selectedHistory[codDevice];
      this.loadSensorDataIfAvailable();
    }
  }


}
