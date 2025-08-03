import { Component, OnInit } from '@angular/core';
import { Employee } from 'app/models/employee';
import { AuthService } from 'app/services/auth.service';
import { EmployeeService } from 'app/services/employee.service';
import { NotificationsService } from 'app/services/notifications.service';
import { ModifEmployee } from 'app/models/modifEmp';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {

  constructor(private authService: AuthService, private empService : EmployeeService,
    private notifService : NotificationsService
  ) { }

  ngOnInit() {
    this.getUser();
  }
  user : Employee = {
    lastName: '', password : '',
    firstName: '',
    address: '',
    mobile: '',
    email : '', id :'' , role : '',
    serre : [], ferme : []
  };
  id : string;
  modifEmp : ModifEmployee = {lastName: '',firstName: '',address: '',mobile: '',email : '', role : '',serre : [], ferme : [], password : ''}

  getUser(){
    console.log(this.authService.CurrentUser)
    this.id = this.authService.CurrentUser.id;
    this.empService.getEmployeeById(this.id).subscribe(res=>{
      this.user = res;
    }) 
  }

  onSubmit() {

    console.log('Formulaire soumis :', this.user);
    // Remplir l'objet modifEmp avec uniquement les IDs
  this.modifEmp = {
    email: this.user.email,
    firstName: this.user.firstName,
    lastName: this.user.lastName,
    address: this.user.address,
    mobile: this.user.mobile,
    role: this.user.role,
    password : this.user.password,
    ferme: (this.user.ferme ?? []).map(f => ({ id: Number(f.id) })),
    serre: (this.user.serre ?? []).map(s => ({ id: Number(s.id) }))

  };

  console.log("Objet à envoyer :", this.modifEmp);
    this.empService.editEmp(this.modifEmp, Number(this.user.id) ).subscribe(res=>{
      this.authService.setCurrentUser(this.user)
      this.notifService.showNotification('top', 'left', "L'employée a été modifié avec succès", 2);
    })
  }

}
