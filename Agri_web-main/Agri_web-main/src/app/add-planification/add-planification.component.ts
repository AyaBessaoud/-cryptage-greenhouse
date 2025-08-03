import { Component, ViewChild } from '@angular/core';
import { MatDatepicker } from '@angular/material/datepicker';
import { FarmService } from 'app/services/farm.service';
import { Farm } from 'app/models/farm';
import { AuthService } from 'app/services/auth.service';
import { CreatePlanification } from 'app/models/createPlanification';
import { GreenHouse } from 'app/models/GreenHouse';
import { endDevice } from 'app/models/endDevice';
import { Actuator } from 'app/models/Actuator';
import { MatCalendarCellCssClasses } from '@angular/material/datepicker';
import { PlanificationService } from 'app/services/planification.service';

interface PlanifDateTimeEntry {
  date: Date; // L'objet Date pour la manipulation interne du calendrier
  time: string; // Format "HH:mm"
}

const DAYS_ORDER_REFERENCE = ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
const MONTHS_ORDER_REFERENCE = ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'];

@Component({
  selector: 'app-add-planification',
  templateUrl: './add-planification.component.html',
  styleUrls: ['./add-planification.component.css']
})
export class AddPlanificationComponent {

  constructor(private authService: AuthService, private farmService: FarmService,
    private service : PlanificationService
  ) { }

  ngOnInit() {
    this.id = this.authService.CurrentUser.id;
    this.GetFarms()
  }


  ListFarms: Farm[] = []
  selectedFarm: Farm | null = null;
  selectedGreenHouse: GreenHouse | null = null;
  id: string;
  planif: CreatePlanification = {
    description: '', pattern: '', etat: 1, dates: [], actuator: { id: 0 }, times: [],
    days: [], months: []
  }
  selectedDevice: endDevice | null = null;

  GetFarms() {
    this.farmService.getAllFarms(this.id).subscribe(res => {
      this.ListFarms = res;
    })
  }

  onSerreChange(actId: string) {
    this.planif.actuator.id = 0;
    this.selectedDevice = null;
    this.selectedActuator = null;
    console.log("planif", this.planif);
  }

  onFarmChange() {
    this.planif.actuator.id = 0;
    this.selectedDevice = null;
    this.selectedActuator = null;
    console.log("planif", this.planif);
  }

  SelectDevice(dev: endDevice | null) {
    this.planif.actuator.id = 0;
    console.log("planif", this.planif);
  }

  selectedActuator: Actuator | null = null;

  selectActuator(act: Actuator) {
    this.selectedActuator = act;
    this.planif.actuator.id = Number(act.id); // met à jour l'ID de l'actionneur sélectionné
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

  onSubmit() {
    console.log("planif_submit", this.planif)
    console.log("dates custom",this.planifDateTimeEntries)
    switch (this.planif.pattern) {
      case 'custom':
        this.planif.dates = this.planifDateTimeEntries.map(entry => {
            const year = entry.date.getFullYear();
            const month = (entry.date.getMonth() + 1).toString().padStart(2, '0'); // Mois sont 0-indexés, +1 et pad
            const day = entry.date.getDate().toString().padStart(2, '0'); // Pad pour le jour
            return `${year}-${month}-${day}`;
          });
        this.planif.times = this.planifDateTimeEntries.map(entry => entry.time);
        this.planif.days = []
        this.planif.months = []
        break;
      case 'daily':
        this.planif.days = this.dailySelectedDays;
        this.planif.times = this.dailySelectedTimes;
        this.planif.dates = [];
        this.planif.months = []
        break;
      case 'monthly':
        this.planif.dates = [];
        this.planif.months = []
        this.planif.days = this.monthlySelectedDays;
        this.planif.times = this.monthlySelectedTimes; // NOUVEAU: Utilise 'times' pour les heures mensuelles
        break;
      case 'yearly':
        this.planif.dates = [];
        this.planif.months = this.yearlySelectedMonths;
        this.planif.days = this.yearlySelectedDaysOfWeek;
        this.planif.times = this.yearlySelectedTimes; // NOUVEAU: Utilise 'times' pour les heures annuelles
        break;
    }
    this.service.createPlanif(this.planif).subscribe(res=>{
      console.log(res)
      this.planif = {
    description: '', pattern: '', etat: 1, dates: [], actuator: { id: 0 }, times: [],
    days: [], months: []
  }
    })
  }

}
