import { Component } from '@angular/core';
import { FarmService } from 'app/services/farm.service';
import { Farm } from 'app/models/farm';
import { AddFarm } from 'app/models/addFarm';
import { ModifFarm } from 'app/models/modifFarm';
import { NotificationsService } from 'app/services/notifications.service';
import { AuthService } from 'app/services/auth.service';
import * as $ from 'jquery';



@Component({
  selector: 'app-get-farm',
  templateUrl: './get-farm.component.html',
  styleUrls: ['./get-farm.component.css']
})
export class GetFarmComponent {

  constructor(private farmService : FarmService, private notificationService: NotificationsService,
    private authService: AuthService
  ){}

  listFarms : Farm[] = []
  selectedFarm : Farm = {id : 0, description : '', serres : []}
  selectedFarm1 : Farm = {id : 0, description : '', serres : []}
  addFarm : AddFarm = {description : '', serres : [] }
  modifFarm : ModifFarm = {description : '', serres: []}
  id : string;
  role : string ='';
  ngOnInit(){
    this.id = this.authService.CurrentUser.id;
    this.role = this.authService.CurrentUser.role;
    this.GetAllFarms();
  }

  GetAllFarms(){
    this.farmService.getAllFarms(this.id).subscribe(res=>{
      this.listFarms = res;
      console.log("liste ferme", this.listFarms)
    })
  }

openSerresModal(ferme : Farm){
  this.selectedFarm = ferme;
  $('#fermeModal').modal('show'); // Utilise jQuery Bootstrap modal
}

openModalAjout(){
   $('#addFarm').modal('show'); // Utilise jQuery Bootstrap modal
}

onSubmit(){
  console.log("à ajouter", this.addFarm)
  this.farmService.addFarm(this.addFarm).subscribe(res=>{
    // 1. Ajouter la ferme retournée dans la liste
    this.listFarms.push(res);
    this.addFarm.description = '';
    this.notificationService.showNotification('top', 'left', "La ferme a été ajoutée avec succès",2);
    // 2. Fermer la modal manuellement avec Bootstrap JS
   $('#addFarm').modal('hide');
  })
}

openModalModif(ferme : Farm){
  this.selectedFarm1 = ferme;
  this.selectedFarm = {...this.selectedFarm1};
  console.log("selectef farm", this.selectedFarm)
  $('#modifFarm').modal('show'); // Utilise jQuery Bootstrap modal
}

ModifierFerme(){
  this.modifFarm.description = this.selectedFarm.description;
  this.modifFarm.serres = this.selectedFarm.serres.map(serre => ({ id: Number(serre.id) }));
  console.log("modifFarm",this.modifFarm)
  this.farmService.modifFarm(this.modifFarm, Number(this.selectedFarm.id)).subscribe(res=>{
    console.log("resss", res)
    this.notificationService.showNotification('top', 'left', "La ferme a été modifiée avec succès",2);
    // Mettre à jour la ferme modifiée dans la liste des fermes
    const index = this.listFarms.findIndex(farm => Number(farm.id) === Number(res.id));
    if (index !== -1) {
      this.listFarms[index] = res; // Remplace l'ancien objet par le nouveau
    }
    $('#modifFarm').modal('hide');
  })
}

}
