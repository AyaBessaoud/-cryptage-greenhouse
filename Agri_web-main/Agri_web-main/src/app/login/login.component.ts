import { Component, OnInit } from '@angular/core';
import { AuthRequest } from 'app/models/authRequest';
import { AuthService } from 'app/services/auth.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  constructor(public authService: AuthService, private router: Router ) { }
  showMessage : boolean = false;

  ngOnInit(): void {
  }

  Submit(form : AuthRequest){
    console.log(form);
    this.authService.logIn(form)
    .subscribe({
      next: (res : any) => {
        console.log("res", res);
        localStorage.setItem("currentUser",JSON.stringify(res.employee) );
        localStorage.setItem("Token", res.token);
      (<HTMLFormElement>document.getElementById("form")).reset();
      this.router.navigateByUrl('/dashboard');
      },
      error: (error) => {
        this.showMessage = true;
        console.log(this.showMessage);
      }
    });
  }

}
