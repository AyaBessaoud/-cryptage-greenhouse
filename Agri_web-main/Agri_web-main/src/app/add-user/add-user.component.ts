import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AddEmployee } from 'app/models/addEmployee';
import { EmployeeService } from 'app/services/employee.service';
import { NotificationsService } from 'app/services/notifications.service';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.css']
})
export class AddUserComponent {

  constructor( public empServices : EmployeeService, private notificationService: NotificationsService){}

  save(form : AddEmployee) {
    console.log(form);
    this.empServices.addEmployee(form).subscribe(res=>{
      console.log(res);
      this.notificationService.showNotification('top', 'left', "L'employée a été ajouté avec succès",2);
      (<HTMLFormElement>document.getElementById("form")).reset();  //pour vider les champs d'un formulaire
     // ($('#exampleModal') as any).modal('show') //affichage d'une modal en cas de success
    },err=>{
      if (err.status === 409) {
        ($('#exampleModal2') as any).modal('show')   ////affichage d'une modal en cas où l'email est déja utilisé
      } else {
        console.log(err);
        alert("An error occurred. Please try again later.");
      }
    }
    );
  }
}
