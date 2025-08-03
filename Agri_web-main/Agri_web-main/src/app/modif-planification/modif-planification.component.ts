import { Component } from '@angular/core';
import { Planification } from 'app/models/Planification';
import { PlanificationService } from 'app/services/planification.service';
import { ActivatedRoute } from '@angular/router';
import { GreenHouse } from 'app/models/GreenHouse';
import { endDevice } from 'app/models/endDevice';
import { Actuator } from 'app/models/Actuator';
import { MatCalendarCellCssClasses } from '@angular/material/datepicker';
import { FarmService } from 'app/services/farm.service';
import { Farm } from 'app/models/farm';
import { AuthService } from 'app/services/auth.service';
import { EnDeviceService } from 'app/services/en-device.service';

interface PlanifDateTimeEntry {
  date: Date; // L'objet Date pour la manipulation interne du calendrier
  time: string; // Format "HH:mm"
}

const DAYS_ORDER_REFERENCE = ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
const MONTHS_ORDER_REFERENCE = ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'];

@Component({
  selector: 'app-modif-planification',
  templateUrl: './modif-planification.component.html',
  styleUrls: ['./modif-planification.component.css']
})
export class ModifPlanificationComponent {

  constructor(private service: PlanificationService, private router: ActivatedRoute,
    private authService: AuthService, private farmService: FarmService, private deviceServices: EnDeviceService
  ) { }

  ngOnInit() {
    this.service.getById(this.router.snapshot.params['id']).subscribe(res => {
      this.planif = res;
      console.log("res de planif", this.planif);
      this.id = this.authService.CurrentUser.id;

      this.GetFarms().then(() => {
        this.selectedFarm = this.ListFarms.find(f => f.id === res.actuator.device.serre.ferme.id);
        this.selectedGreenHouse = this.selectedFarm?.serres.find(s => s.id === res.actuator.device.serre.id) ?? null;

        // 1. Récupérer tous les devices
        this.deviceServices.getAllDevices().subscribe(devices => {
          this.devList = devices;

          // 2. Trouver le device qui correspond à celui de la planif
          this.selectedDevice = this.devList.find(d => d.id === res.actuator.device.id);

          // 3. Ensuite, trouver l'actionneur à l'intérieur de ce device
          this.selectedActuator = this.selectedDevice?.localActuators.find(a => a.id === res.actuator.id);

          console.log("selectedDevice", this.selectedDevice);
          console.log("selectedActuator", this.selectedActuator);
        });
      });

      // Peupler les champs spécifiques au pattern de planification
      switch (this.planif.pattern) {
        case 'custom':
          if (this.planif.dates && this.planif.times) {
            this.planifDateTimeEntries = this.planif.dates.map((dateStr, index) => {
              // Convertir la chaîne 'YYYY-MM-DD' en objet Date localement
              // Créer une date en spécifiant année, mois, jour pour éviter les problèmes de fuseau horaire
              const parts = dateStr.split('-');
              const year = parseInt(parts[0], 10);
              const month = parseInt(parts[1], 10) - 1; // Mois 0-indexé
              const day = parseInt(parts[2], 10);
              const dateObj = new Date(year, month, day);

              return {
                date: dateObj,
                time: this.planif.times?.[index] || '09:00' // Assurez-vous que l'heure existe
              };
            });
            // Mettre à jour selectedDates pour l'affichage du calendrier
            this.selectedDates = this.planifDateTimeEntries.map(entry => entry.date);
            this.selectedDates.sort((a, b) => a.getTime() - b.getTime());
            this.planifDateTimeEntries.sort((a, b) => a.date.getTime() - b.date.getTime());
          } else {
            this.planifDateTimeEntries = [];
            this.selectedDates = [];
          }
          break;
        case 'daily':
          this.dailySelectedDays = this.planif.days || [];
          this.dailySelectedTimes = this.planif.times && this.planif.times.length > 0 ? this.planif.times : ['09:00'];
          break;
        case 'monthly':
          this.monthlySelectedDays = this.planif.days || [];
          this.monthlySelectedTimes = this.planif.times && this.planif.times.length > 0 ? this.planif.times : ['09:00'];
          break;
        case 'yearly':
          this.yearlySelectedMonths = this.planif.months || [];
          this.yearlySelectedDaysOfWeek = this.planif.days || [];
          this.yearlySelectedTimes = this.planif.times && this.planif.times.length > 0 ? this.planif.times : ['09:00'];
          break;
      }
    });
  }


  planif: Planification = {
    id: '', etat: 1, dates: [], times: [], months: [], days: [], description: '', blocked: false,
    frame: '', actuator: {
      idU: '', description: '', id: '', index: 0, output: '', longt: 10.3166711, lat: 36.8816580,
      sensors: [], device: {
        id: '', type: '', codDevice: '', nivBat: 254, sensors: [], localActuators: [],
        config: { pa: 1, ps: 1, lat: 36.8816580, longt: 10.3166711 },
        serre: { id: 0, description: '', ferme: { id: 0, description: '', serres: [] }, devices: [] }
      }
    }, creationDate: '',
    pattern: ''
  }
  ListFarms: Farm[] = []
  selectedFarm: Farm | null = null;
  selectedGreenHouse: GreenHouse | null = null;
  id: string;
  selectedDevice: endDevice | null = null;
  devList: endDevice[] = [];

  compareDevices(dev1: any, dev2: any): boolean {
    if (!dev1 && !dev2) return true;
    if (!dev1 || !dev2) return false;

    // Comparaison spéciale si id vide
    if (dev1.id === '' && dev2.id === '') return true;

    return dev1.id === dev2.id;
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

  onSerreChange(actId: string) {
    this.planif.actuator.id = '';
    this.selectedDevice = null;
    this.selectedActuator = null;
    console.log("planif", this.planif);
  }

  onFarmChange() {
    this.planif.actuator.id = '';
    this.selectedDevice = null;
    this.selectedActuator = null;
    console.log("planif", this.planif);
  }

  SelectDevice(dev: endDevice | null) {
    this.planif.actuator.id = '';
    console.log("planif", this.planif);
  }

  selectedActuator: Actuator | null = null;

  selectActuator(act: Actuator) {
    this.selectedActuator = act;
    this.planif.actuator.id = act.id; // met à jour l'ID de l'actionneur sélectionné
    console.log("Actionneur sélectionné :", act);
    console.log("planif", this.planif);
  }

  getActuatorIcon(sensorId: string): string {
    const icons = {
      'Lampe': 'bi-lightbulb-fill',
      'Ventilateur': 'bi-fan',
      'Pompe': 'bi-water',
    };
    return `bi ${icons[sensorId] || 'bi-question-circle'}`; // icône par défaut au cas où
  }

  daysOfWeek: string[] = DAYS_ORDER_REFERENCE;
  monthsOfYear: string[] = MONTHS_ORDER_REFERENCE;

  selectedTime: string;
  selectedDays: string[] = [];
  selectedMonths: string[] = [];
  selectedDates: Date[] = []; // Utilisé pour le surlignage visuel des dates dans le calendrier
  planifDateTimeEntries: PlanifDateTimeEntry[] = [];

  // Gère le changement de type de planification (radio buttons)
  onPlanningTypeChange(): void {
    // Si le type sélectionné est 'daily', cocher tous les jours de la semaine
    if (this.planif.pattern === 'daily') {
      this.dailySelectedDays = [...this.daysOfWeek]; // Copie tous les jours dans dailySelectedDays
    }
  }

  ///déclenchée chaque fois que l'utilisateur clique sur un jour
  onDateSelected(date: Date | null): void {
    if (date) {
      // Comparer les dates par leur année, mois et jour pour une correspondance exacte
      const indexInSelectedDates = this.selectedDates.findIndex(d =>
        d.getFullYear() === date.getFullYear() &&
        d.getMonth() === date.getMonth() &&
        d.getDate() === date.getDate()
      );

      // Trouver l'entrée correspondante dans la liste des paires date-heure
      const indexInPlanifEntries = this.planifDateTimeEntries.findIndex(entry =>
        entry.date.getFullYear() === date.getFullYear() &&
        entry.date.getMonth() === date.getMonth() &&
        entry.date.getDate() === date.getDate()
      );

      if (indexInSelectedDates > -1) {
        // La date est déjà sélectionnée, donc on la désélectionne :
        this.selectedDates.splice(indexInSelectedDates, 1); // Retirer du tableau de surlignage du calendrier
        if (indexInPlanifEntries > -1) {
          this.planifDateTimeEntries.splice(indexInPlanifEntries, 1); // Retirer de la liste des paires date-heure
        }
      } else {
        // La date n'est pas sélectionnée, donc on l'ajoute :
        this.selectedDates.push(date); // Ajouter au tableau de surlignage du calendrier
        this.planifDateTimeEntries.push({ date: date, time: '09:00' }); // Ajouter à la liste des paires avec une heure par défaut
      }
    }
    // Trier les deux tableaux pour maintenir un ordre cohérent
    this.selectedDates.sort((a, b) => a.getTime() - b.getTime());
    this.planifDateTimeEntries.sort((a, b) => a.date.getTime() - b.date.getTime());
    console.log('Paires Date-Heure sélectionnées :', this.planifDateTimeEntries);
  }

  // Fonction utilisée par mat-calendar pour appliquer une classe CSS aux jours sélectionnés
  dateClass() {
    return (date: Date): MatCalendarCellCssClasses => {
      const isSelected = this.selectedDates.some(selectedDate =>
        selectedDate.getFullYear() === date.getFullYear() &&
        selectedDate.getMonth() === date.getMonth() &&
        selectedDate.getDate() === date.getDate()
      );
      return isSelected ? 'selected-day' : ''; // Applique la classe 'selected-day' si la date est sélectionnée
    };
  }

  // Permet de supprimer une paire date-heure de la liste affichée
  removeDateTimeEntry(entryToRemove: PlanifDateTimeEntry): void {
    this.planifDateTimeEntries = this.planifDateTimeEntries.filter(entry => entry !== entryToRemove);
    // Assurer que la date est aussi désélectionnée visuellement dans le calendrier
    this.selectedDates = this.selectedDates.filter(d =>
      !(d.getFullYear() === entryToRemove.date.getFullYear() &&
        d.getMonth() === entryToRemove.date.getMonth() &&
        d.getDate() === entryToRemove.date.getDate())
    );
    this.selectedDates.sort((a, b) => a.getTime() - b.getTime());
    console.log('Paire Date-Heure supprimée. Restantes :', this.planifDateTimeEntries);
  }
  // Pour la planification quotidienne
  dailySelectedDays: string[] = []; // Jours de la semaine sélectionnés par nom (ex: ['Lundi', 'Mardi'])
  dailySelectedTimes: string[] = ['09:00']; // Tableau des heures pour la planification quotidienne, initialisé avec une heure par défaut

  //Pour la planification quotidienne (jours de la semaine)
  onDailyDayChange(dayValue: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    if (isChecked) {
      this.dailySelectedDays.push(dayValue);
    } else {
      this.dailySelectedDays = this.dailySelectedDays.filter(day => day !== dayValue);
    }
    this.dailySelectedDays.sort((a, b) => DAYS_ORDER_REFERENCE.indexOf(a) - DAYS_ORDER_REFERENCE.indexOf(b));
    console.log('Jours quotidiens sélectionnés :', this.dailySelectedDays);
  }

  // NOUVELLE FONCTION: Ajouter une nouvelle heure pour la planification quotidienne
  addDailyTime(): void {
    this.dailySelectedTimes.push('09:00'); // Ajoute une heure par défaut
  }

  // NOUVELLE FONCTION: Supprimer une heure de la planification quotidienne
  removeDailyTime(index: number): void {
    if (this.dailySelectedTimes.length > 1) { // S'assurer qu'il reste au moins une heure
      this.dailySelectedTimes.splice(index, 1);
    } else {
      console.log('Au moins une heure doit être sélectionnée.');
      // Vous pourriez afficher un message à l'utilisateur ici
    }
  }

  // Pour la planification mensuelle
  monthlySelectedDays: string[] = []; // Jours du mois sélectionnés (ex: ['1', '15'])
  monthlySelectedTimes: string[] = ['09:00'];

  // Pour la planification mensuelle
  onMonthlyDayChange(dayValue: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    if (isChecked) {
      this.monthlySelectedDays.push(dayValue);
    } else {
      this.monthlySelectedDays = this.monthlySelectedDays.filter(day => day !== dayValue);
    }
    // Trie les jours dans l'ordre de la semaine en utilisant la référence
    this.monthlySelectedDays.sort((a, b) => DAYS_ORDER_REFERENCE.indexOf(a) - DAYS_ORDER_REFERENCE.indexOf(b));
  }

  addMonthlyTime(): void {
    this.monthlySelectedTimes.push('09:00');
  }

  removeMonthlyTime(index: number): void {
    if (this.monthlySelectedTimes.length > 1) {
      this.monthlySelectedTimes.splice(index, 1);
    } else {
      console.log('Au moins une heure doit être sélectionnée pour la planification mensuelle.');
    }
  }

  // Pour la planification annuelle
  yearlySelectedMonths: string[] = [];
  yearlySelectedDaysOfWeek: string[] = [];
  yearlySelectedTimes: string[] = ['09:00'];

  onYearlyMonthChange(monthValue: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    if (isChecked) {
      this.yearlySelectedMonths.push(monthValue);
    } else {
      this.yearlySelectedMonths = this.yearlySelectedMonths.filter(month => month !== monthValue);
    }
    this.yearlySelectedMonths.sort((a, b) => MONTHS_ORDER_REFERENCE.indexOf(a) - MONTHS_ORDER_REFERENCE.indexOf(b));
  }

  onYearlyDayOfWeekChange(dayValue: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    if (isChecked) {
      this.yearlySelectedDaysOfWeek.push(dayValue);
    } else {
      this.yearlySelectedDaysOfWeek = this.yearlySelectedDaysOfWeek.filter(day => day !== dayValue);
    }
    this.yearlySelectedDaysOfWeek.sort((a, b) => DAYS_ORDER_REFERENCE.indexOf(a) - DAYS_ORDER_REFERENCE.indexOf(b));
  }

  // NOUVELLE FONCTION: Ajouter une nouvelle heure pour la planification annuelle
  addYearlyTime(): void {
    this.yearlySelectedTimes.push('09:00');
  }

  // NOUVELLE FONCTION: Supprimer une heure de la planification annuelle
  removeYearlyTime(index: number): void {
    if (this.yearlySelectedTimes.length > 1) {
      this.yearlySelectedTimes.splice(index, 1);
    } else {
      console.log('Au moins une heure doit être sélectionnée pour la planification annuelle.');
    }
  }

  onToggle(event: any) {
    const isChecked = event.target.checked; // true ou false
    this.planif.etat = isChecked ? 1 : 0;
    console.log('planif_switch', this.planif)
  }
}
