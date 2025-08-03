import { Component, OnInit, ElementRef } from '@angular/core';
import { ROUTES } from '../sidebar/sidebar.component'; //Liste des routes définies dans le fichier sidebar.component.ts
import {Location, LocationStrategy, PathLocationStrategy} from '@angular/common'; // Permet d'obtenir l'URL actuelle
import { Router } from '@angular/router'; 
import { NotificationsService } from 'app/services/notifications.service';
import { AuthService } from 'app/services/auth.service';
import 'bootstrap-material-design'; // (ça charge le JS)
declare var $: any;

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
    private listTitles: any[];  //Liste des éléments de navigation (ROUTES).
    location: Location;  //Instance de Location permettant de récupérer l’URL actuelle.
      mobile_menu_visible: any = 0;  // (0 = caché, 1 = visible
    private toggleButton: any;  // Bouton permettant d’ouvrir/fermer la sidebar
    private sidebarVisible: boolean;  //État de la sidebar (true = affichée, false = cachée).

    constructor(location: Location,  private element: ElementRef, private router: Router,
        private notificationService: NotificationsService, private authService: AuthService) {
      this.location = location;
          this.sidebarVisible = false;
    }
    

    notifications$ = this.notificationService.notifications$; // Écoute les notifications en direct

    ngOnInit(){
        const userId = this.authService.CurrentUser.id;
        this.notificationService.getNotification(userId);

        $('#navbarDropdownMenuLink').dropdown();
      this.listTitles = ROUTES.filter(listTitle => listTitle);  //Récupère et stocke la liste des routes de navigation définies dans sidebar.component.ts
      const navbar: HTMLElement = this.element.nativeElement;  
      this.toggleButton = navbar.getElementsByClassName('navbar-toggler')[0]; //Stocke le bouton de basculement du menu (.navbar-toggler)
      this.router.events.subscribe((event) => {
        this.sidebarClose();  //À chaque changement de route La sidebar est fermée.
         var $layer: any = document.getElementsByClassName('close-layer')[0];
         if ($layer) {
           $layer.remove();
           this.mobile_menu_visible = 0;
         }
     });
    }

    //pour la suppression du notif
    delete(id: string): void {
    this.notificationService.deleteNotif(id).subscribe(() => {
    this.notificationService.removeNotificationFromList(id);
  });
}



    sidebarOpen() {
        const toggleButton = this.toggleButton;
        const body = document.getElementsByTagName('body')[0];
        setTimeout(function(){ //Ajoute la classe toggled au bouton après 500ms
            toggleButton.classList.add('toggled');
        }, 500);

        body.classList.add('nav-open'); //Ajoute la classe nav-open au <body> pour indiquer que le menu est ouvert
        //Met à jour this.sidebarVisible à true
        this.sidebarVisible = true;
    };
    sidebarClose() {
        const body = document.getElementsByTagName('body')[0];
        this.toggleButton.classList.remove('toggled');
        this.sidebarVisible = false;
        body.classList.remove('nav-open');
    };
    sidebarToggle() {
        var $toggle = document.getElementsByClassName('navbar-toggler')[0];

        if (this.sidebarVisible === false) {
            this.sidebarOpen();
        } else {
            this.sidebarClose();
        }
        const body = document.getElementsByTagName('body')[0];

        if (this.mobile_menu_visible == 1) {
            // $('html').removeClass('nav-open');
            body.classList.remove('nav-open');
            if ($layer) {
                $layer.remove();
            }
            setTimeout(function() {
                $toggle.classList.remove('toggled');
            }, 400);

            this.mobile_menu_visible = 0;
        } else {
            setTimeout(function() {
                $toggle.classList.add('toggled');
            }, 430);

            var $layer = document.createElement('div');
            $layer.setAttribute('class', 'close-layer');


            if (body.querySelectorAll('.main-panel')) {
                document.getElementsByClassName('main-panel')[0].appendChild($layer);
            }else if (body.classList.contains('off-canvas-sidebar')) {
                document.getElementsByClassName('wrapper-full-page')[0].appendChild($layer);
            }

            setTimeout(function() {
                $layer.classList.add('visible');
            }, 100);

            $layer.onclick = function() { //asign a function
              body.classList.remove('nav-open');
              this.mobile_menu_visible = 0;
              $layer.classList.remove('visible');
              setTimeout(function() {
                  $layer.remove();
                  $toggle.classList.remove('toggled');
              }, 400);
            }.bind(this);

            body.classList.add('nav-open');
            this.mobile_menu_visible = 1;

        }
    };

    getTitle(){
      var titlee = this.location.prepareExternalUrl(this.location.path()); //Récupère l'URL actuelle (this.location.path()
      if(titlee.charAt(0) === '#'){  //Supprime le # au début de l’URL si nécessaire
          titlee = titlee.slice( 1 );
      }

      for(var item = 0; item < this.listTitles.length; item++){
       
          if(this.listTitles[item].path === titlee){  //Recherche le titre correspondant dans this.listTitles et le retourne
              return this.listTitles[item].title;
          }
      }
      
      if(titlee === '/addUser'){  //Recherche le titre correspondant dans this.listTitles et le retourne
        return 'Gestion d utilisateur';
    }
    }
}
