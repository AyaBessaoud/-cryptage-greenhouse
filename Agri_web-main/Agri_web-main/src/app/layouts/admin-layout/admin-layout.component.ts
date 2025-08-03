import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Location, LocationStrategy, PathLocationStrategy, PopStateEvent } from '@angular/common'; //Fournit des outils pour manipuler l'historique de navigation.
import { Router, NavigationEnd, NavigationStart } from '@angular/router';  //Permet d’écouter les événements de navigation.
import PerfectScrollbar from 'perfect-scrollbar'; //Une bibliothèque JavaScript pour améliorer la gestion du défilement (scroll).
import * as $ from "jquery";  //Permet d’utiliser jQuery pour manipuler le DOM
import { filter, Subscription } from 'rxjs';  //Utilisés pour filtrer les événements de navigation et gérer les abonnements.

@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss']
})
export class AdminLayoutComponent implements OnInit {
  private _router: Subscription;  //Stocke l'abonnement aux événements de navigation.
  private lastPoppedUrl: string;  //Stocke la dernière URL avant un changement de navigation.
  private yScrollStack: number[] = [];  //Utilisé pour sauvegarder la position de défilement (scroll) lorsque l'utilisateur navigue.

  constructor( public location: Location, private router: Router) {}

  ngOnInit() {
      //Vérifie si l'utilisateur utilise Windows (navigator.platform).
      const isWindows = navigator.platform.indexOf('Win') > -1 ? true : false;

      if (isWindows && !document.getElementsByTagName('body')[0].classList.contains('sidebar-mini')) {
          // Si l'utilisateur est sous Windows Active PerfectScrollbar en ajoutant la classe perfect-scrollbar-on au <body>
          document.getElementsByTagName('body')[0].classList.add('perfect-scrollbar-on');
      } else { //Sinon, il désactive l'effet en retirant perfect-scrollbar-off
          document.getElementsByTagName('body')[0].classList.remove('perfect-scrollbar-off');
      }
      const elemMainPanel = <HTMLElement>document.querySelector('.main-panel'); //Récupère l'élément principal contenant l'affichage principal
      const elemSidebar = <HTMLElement>document.querySelector('.sidebar .sidebar-wrapper');  // Récupère l'élément .sidebar-wrapper qui contient le menu latéral.
     
      //gestion avancée de la navigation et du défilement
      this.location.subscribe((ev:PopStateEvent) => {   //Écoute les changements dans l'historique du navigateur
          this.lastPoppedUrl = ev.url; //Stocke l'URL précédente pour comparer avec la prochaine navigation.
      });
       this.router.events.subscribe((event:any) => { //Écoute tous les événements de navigation
          //Gestion du début de navigation (NavigationStart)
          //Gérer le défilement lorsque l’utilisateur revient en arrière, pour éviter de toujours repartir en haut de la page.
          if (event instanceof NavigationStart) {
             if (event.url != this.lastPoppedUrl)   //Si l'URL n'est pas celle du dernier PopStateEvent, 
                 this.yScrollStack.push(window.scrollY);  //alors on sauvegarde la position du scroll actuel (window.scrollY) dans yScrollStack.
         } //Gestion de la fin de navigation (NavigationEnd)
         else if (event instanceof NavigationEnd) {
             if (event.url == this.lastPoppedUrl) { //Si l’URL est égale à lastPoppedUrl
                 this.lastPoppedUrl = undefined;  
                 window.scrollTo(0, this.yScrollStack.pop());  //Si l’URL est égale à lastPoppedUrl
             } else
                 window.scrollTo(0, 0);   //On remonte en haut de la page (window.scrollTo(0, 0)).
         }
      });  
      //Quand on change de page, on revient automatiquement en haut.
      this._router = this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe((event: NavigationEnd) => {
           elemMainPanel.scrollTop = 0; //Remonte la page principale en haut.
           elemSidebar.scrollTop = 0;  //Remonte la barre latérale en haut.
      });
      //Améliorer l’expérience de défilement sur les PC Windows.
      if (window.matchMedia(`(min-width: 960px)`).matches && !this.isMac()) {  //Vérifie si la largeur de l'écran est supérieur
          let ps = new PerfectScrollbar(elemMainPanel);  //Active PerfectScrollbar sur main-panel (contenu principal).
          ps = new PerfectScrollbar(elemSidebar);  //Active PerfectScrollbar sur sidebar (menu latéral).
      }

      const window_width = $(window).width();  //Obtient la largeur actuelle de la fenêtre.
      let $sidebar = $('.sidebar');  //Sélectionne la barre latérale.
      let $sidebar_responsive = $('body > .navbar-collapse');  //Sélectionne le menu responsive.
      let $sidebar_img_container = $sidebar.find('.sidebar-background');  //Trouve l’image de fond de la sidebar.

    }
  ngAfterViewInit() {
      this.runOnRouteChange();
  }
  isMaps(path){
      var titlee = this.location.prepareExternalUrl(this.location.path());  //Convertit l'URL en un format utilisable pour l'application Angular.
      titlee = titlee.slice( 1 ); //Supprime le premier / de l'URL exemple supprime / dans /maps
      if(path == titlee){
          return false;
      }
      else {
          return true;
        }
    }
  runOnRouteChange(): void {
    if (window.matchMedia(`(min-width: 960px)`).matches && !this.isMac()) { //Vérifie si l'écran a une largeur d'au moins 960p + l'utilisateur n'est pas sur un Mac
      const elemMainPanel = <HTMLElement>document.querySelector('.main-panel'); //Récupérer le conteneur principal où PerfectScrollbar sera appliqué
      const ps = new PerfectScrollbar(elemMainPanel);  //rée une nouvelle instance de PerfectScrollbar pour gérer le scroll personnalisé sur elemMainPanel
      ps.update(); //Met à jour PerfectScrollbar pour réajuster l'affichage en fonction des nouveaux éléments ajoutés après le changement de route
    }
  }
  isMac(): boolean {
      let bool = false;
      if (navigator.platform.toUpperCase().indexOf('MAC') >= 0 || navigator.platform.toUpperCase().indexOf('IPAD') >= 0) {
          bool = true;
      }
      return bool;
  }

}
