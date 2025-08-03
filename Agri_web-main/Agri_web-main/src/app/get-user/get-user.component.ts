import { Component } from '@angular/core';
import { Employee } from 'app/models/employee';
import { EmployeeService } from 'app/services/employee.service';
import { NotificationsService } from 'app/services/notifications.service';
import { Farm } from 'app/models/farm';
import { FarmService } from 'app/services/farm.service';
import { GreenHouseService } from 'app/services/green-house.service';
import { GreenHouse } from 'app/models/GreenHouse';
import { ModifEmployee } from 'app/models/modifEmp';
import { AuthService } from 'app/services/auth.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-get-user',
  templateUrl: './get-user.component.html',
  styleUrls: ['./get-user.component.css']
})
export class GetUserComponent {

  constructor( public empServices : EmployeeService, private notificationService: NotificationsService,
    private farmService : FarmService, private serreService: GreenHouseService,
    private authService: AuthService, private cdRef: ChangeDetectorRef
  ){}

  ngOnInit(): void {
    this.id = this.authService.CurrentUser.id;
    this.getAllEmployee() ;
    this.GetAllFarms();
    this.GetAllGreenHouses();
  }
  empList : Employee[] = [];
  employee : Employee = {id : "", firstName : "", lastName:"", mobile:"", address: "",email : "", role : "",
    serre: [], ferme : [], password : ''
  };
  listFarms : Farm[] = []
  selectedFarms : Farm[] = []
  ListSerres : GreenHouse[] = []
  selectedGreenhousesMap: { [farmId: number]: GreenHouse[] } = {};
  currentFarmIndex = 0;
  modifEmp : ModifEmployee = {lastName: '',firstName: '',address: '',mobile: '',email : '', role : '',serre : [], ferme : [],
    password : ''
  }
  id : string;


  //recuperation de tous les utilisateurs
  getAllEmployee() {
    this.empServices.getAllEmployee().subscribe(res=>{
      this.empList = res;
      this.cdRef.detectChanges();  // force Angular à mettre à jour la vue
      console.log("empp", res)
    },err=>{
      console.log("error while fetching data.")
    });
  }

  GetAllGreenHouses(){
    this.serreService.getAll(this.id).subscribe(res=>{
      this.ListSerres = res;
    })
  }

  GetAllFarms(){
    this.farmService.getAllFarms(this.id).subscribe(res=>{
      this.listFarms = res;
    })
  }

  SelectPerson(emp: Employee) {
    this.employee = emp;  // On stocke l'employé sélectionné dans une variable
    console.log("selected emp", this.employee)
    // Initialiser les fermes sélectionnées
    this.selectedFarms = Array.isArray(emp.ferme) ? [...emp.ferme] : []; //On copie les fermes déjà associées à cet employé dans un tableau

    this.selectedGreenhousesMap = {}; //On initialise une map vide pour stocker les serres sélectionnées
    //Pour chaque ferme déjà assignée à l'employé, on filtre les serres qui lui sont liées
    // // et on les stocke dans selectedGreenhousesMap
    // On parcourt les fermes uniquement si c'est un tableau non vide
    if (Array.isArray(emp.ferme)) {
      console.log("ferme true:");
      for (const farm of emp.ferme) {
        if (Array.isArray(emp.serre)) {
          console.log("serre true:");
          this.selectedGreenhousesMap[farm.id] = emp.serre.filter(serre => Number(serre.ferme.id) === Number(farm.id));
        } else {
          this.selectedGreenhousesMap[farm.id] = [];
        }
      }
    }
    console.log("Selected greenhouses map:", this.selectedGreenhousesMap);

    ($('#FarmModal') as any).modal('show'); //on  affiche la liste des fermes.
  }

  //Gestion des clics sur fermes  appelée lorsqu’on clique sur une ferme.
  toggleFarmSelection(farm: Farm) {
    const index = this.selectedFarms.findIndex(f => Number(f.id) === Number(farm.id)); //On cherche si la ferme est déjà sélectionnée
    if (index > -1) { //Si oui, on la retire de la sélection, et on supprime les serres associées
      this.selectedFarms.splice(index, 1);
      delete this.selectedGreenhousesMap[farm.id];
    } else { //Sinon, on l’ajoute et on crée une entrée vide pour les serres associées
      this.selectedFarms.push(farm);
      this.selectedGreenhousesMap[farm.id] = [];
    }
  }

  //Ouvrir les modals des serres, une par une
  openGreenhouseModals() {
    ($('#FarmModal') as any).modal('hide');
    this.currentFarmIndex = 0; //On cache la modale des fermes.
    this.showNextGreenhouseModal(); //On commence à parcourir les serres pour la première ferme sélectionnée
  }

  showNextGreenhouseModal() {
    if (this.currentFarmIndex < this.selectedFarms.length) {  // Affiche la modale de serres correspondant à currentFarmIndex
      const currentFarm = this.selectedFarms[this.currentFarmIndex];
      this.ListSerres = currentFarm.serres; //On récupère les serres liées à la ferme courante
      ($('#serreModal') as any).modal('show');
    } else {
      // Final step: on met à jour les données de l'employé avec les fermes et serres sélectionnées,
      console.log("Updated employee", this.employee);
      this.submitSelections()
    }
  }
  //Appelé lorsqu’on clique sur "Suivant" dans une modale de serre
  nextSerreModal() {
    console.log("currentFarmIndex before increment:", this.currentFarmIndex);
    ($('#serreModal') as any).modal('hide');
    this.currentFarmIndex++;  // On passe à la ferme suivante.
    console.log("currentFarmIndex after increment:", this.currentFarmIndex);
    setTimeout(() => this.showNextGreenhouseModal(), 500);
  }

  // Gère la sélection/désélection d’une serre pour la ferme courante
  toggleSerreSelection(serre: GreenHouse) {
    const farmId = this.selectedFarms[this.currentFarmIndex].id; // On récupère l'id de la ferme actuellement affichée.
    const selectedList = this.selectedGreenhousesMap[farmId] || []; // On récupère la liste des serres déjà sélectionnées pour cette ferme 
    // Si la serre est déjà sélectionnée, on la retire ; sinon, on l’ajoute.
    const index = selectedList.findIndex(s => Number(s.id) === Number(serre.id));
    if (index > -1) {
      selectedList.splice(index, 1);
    } else {
      selectedList.push(serre);
    }
    //Mise à jour de la map
    this.selectedGreenhousesMap[farmId] = selectedList;
    console.log("select Serre", this.selectedGreenhousesMap)
  }

  isFarmSelected(farm: Farm): boolean {
  return this.selectedFarms.some(f => f.id.toString() === farm.id.toString());
}

isSerreSelected(serre: GreenHouse, farmId: string): boolean {
  return this.selectedGreenhousesMap[farmId]?.some(s => s.id.toString() === serre.id.toString());
}

submitSelections(){
  // Remplir l'objet modifEmp avec uniquement les IDs
  this.modifEmp = {
    email: this.employee.email,
    firstName: this.employee.firstName,
    lastName: this.employee.lastName,
    address: this.employee.address,
    mobile: this.employee.mobile,
    role: this.employee.role,
    password: this.employee.password,
    ferme: this.selectedFarms ? this.selectedFarms.map(f => ({ id: Number(f.id) })) : [],
    serre: this.selectedGreenhousesMap
      ? Object.values(this.selectedGreenhousesMap).flat().map(s => ({ id: Number(s.id) }))
      : []
  };

  console.log("Objet à envoyer :", this.modifEmp);
  this.empServices.editEmp(this.modifEmp, Number(this.employee.id) ).subscribe(res=>{
      this.notificationService.showNotification('top', 'left', "L'employée a été modifié avec succès",2);
      ($('#serreModal') as any).modal('hide');
      console.log("modifier", res)
      //Mettre à jour l'employé dans empList
     const index = this.empList.findIndex(emp => Number(emp.id) === Number(this.employee.id));
      if (index !== -1) {
        // Remplace l'ancien objet par une copie mise à jour (tu peux adapter selon ce que tu veux afficher)
        this.empList[index] = {
          ...this.employee,
          ferme: [...this.selectedFarms],
          serre: res.serre
        };
      }
    //this.getAllEmployee()
    })
}

  deleteEmployee(ids: any){
    const id: number = Number(ids);
    this.empServices.deleteEmp(id).subscribe(res =>{
      this.empList = this.empList.filter(emp => Number(emp.id) !== id)
      this.notificationService.showNotification('top', 'left', "L'employée a été supprimé avec succès",2);
    })
  }

}
