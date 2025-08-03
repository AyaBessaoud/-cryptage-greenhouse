import { Component } from '@angular/core';
import { GreenHouseService } from 'app/services/green-house.service';
import { GreenHouse } from 'app/models/GreenHouse';
import { FarmService } from 'app/services/farm.service';
import { Farm } from 'app/models/farm';
import { ModifGreenHouse } from 'app/models/modifGreenHouse';
import { NotificationsService } from 'app/services/notifications.service';
import { AuthService } from 'app/services/auth.service';
import * as $ from 'jquery';

@Component({
  selector: 'app-green-house',
  templateUrl: './green-house.component.html',
  styleUrls: ['./green-house.component.css']
})
export class GreenHouseComponent {

  constructor(private serreService: GreenHouseService, private farmService : FarmService,
    private notificationService: NotificationsService,private authService: AuthService
  ){}

  ListSerres : GreenHouse[] = []
  ListFarms : Farm[] = []
  selectedGhouse : GreenHouse = { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []}
  selectedGhouse1 : GreenHouse = { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []}
  modifSerre : ModifGreenHouse = { description : '', ferme : {id : 0}, devices : []} 
  id : string
  role : string ='';
  ngOnInit(){
    this.id = this.authService.CurrentUser.id;
    this.role = this.authService.CurrentUser.role;
    this.GetAllGreenHouses()
    this.GetFarms()
  }

  GetFarms(){
    this.farmService.getAllFarms(this.id).subscribe(res=>{
      this.ListFarms = res;
    })
  }

  GetAllGreenHouses(){
    this.serreService.getAll(this.id).subscribe(res=>{
      console.log("ress", res)
      this.ListSerres = res;
    })
  }

  openDevModal(serre : GreenHouse){
    this.selectedGhouse = serre;
    $('#SerreModal').modal('show'); // Utilise jQuery Bootstrap modal
  }
  openModifModal(serre : GreenHouse){
    this.selectedGhouse1 = serre;
    this.selectedGhouse = {...this.selectedGhouse1};
    console.log("serre à modifier", this.selectedGhouse)
    $('#modifSerre').modal('show'); // Utilise jQuery Bootstrap modal
  }

  openModalAjout(){
     this.selectedGhouse = { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []}
     this.modifSerre= { description : '', ferme : {id : 0}, devices : []} ;
     $('#addSerre').modal('show'); // Utilise jQuery Bootstrap modal
  }

  compareDevices(dev1: any, dev2: any): boolean {
    return dev1 && dev2 ? Number(dev1.id) === Number(dev2.id) : dev1 === dev2;
  }

  modifierSerre(){
    this.modifSerre.description = this.selectedGhouse.description;
    if(this.selectedGhouse.ferme != null){
      this.modifSerre.ferme = { id: Number(this.selectedGhouse.ferme.id) }; // créer l'objet avant d'y mettre l'id
    }else{
      this.modifSerre.ferme = null;
    }
    this.modifSerre.devices = this.selectedGhouse.devices.map(dev=> ({id : Number(dev.id)}))
    console.log("modif serre",this.modifSerre)
    this.serreService.modifGreenHouse(this.modifSerre, Number(this.selectedGhouse.id)).subscribe(res=>{
      console.log("ress modif", res)
      this.notificationService.showNotification('top', 'left', "La serre a été modifiée avec succès",2);
      // Mettre à jour la ferme modifiée dans la liste des fermes
    const index = this.ListSerres.findIndex(serre => Number(serre.id) === Number(res.id));
    if (index !== -1) {
      this.ListSerres[index] = res; // Remplace l'ancien objet par le nouveau
    }
      $('#modifSerre').modal('hide'); 
    })
  }

  SelectFerme(ferme : Farm){
    console.log("changed farm", this.selectedGhouse)
    this.selectedGhouse.ferme = ferme;
  }

  addGreenHouse(){
     this.modifSerre.description = this.selectedGhouse.description;
    if(this.selectedGhouse.ferme != null){
      this.modifSerre.ferme = { id: Number(this.selectedGhouse.ferme.id) }; // créer l'objet avant d'y mettre l'id
    }else{
      this.modifSerre.ferme = null;
    }
    this.modifSerre.devices = this.selectedGhouse.devices.map(dev=> ({id : Number(dev.id)}))
    console.log("ajout serre",this.modifSerre)
    this.serreService.addGHouse(this.modifSerre).subscribe(res=>{
      this.ListSerres.push(res);
      this.selectedGhouse = { id :0, description : '', ferme :{ id:0, description : '', serres : []}, devices : []}
     this.modifSerre= { description : '', ferme : {id : 0}, devices : []} ;
     this.notificationService.showNotification('top', 'left', "La serre a été ajoutée avec succès",2);
     $('#addSerre').modal('hide');
    })
  }
}
