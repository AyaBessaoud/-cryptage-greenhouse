import { Component, OnInit } from '@angular/core';
import { AuthService } from 'app/services/auth.service';

declare const $: any;
declare interface RouteInfo {
    path: string;
    title: string;
    icon: string;
    class: string;
}
export const ROUTES: RouteInfo[] = [
    { path: '/dashboard', title: 'Dashboard',  icon: 'dashboard', class: '' },
    { path: '/maps', title: 'Maps',  icon:'location_on', class: '' },
    { path: '/users', title: 'Utilisateurs',  icon:'person', class: '' },
    {path: '/getFarm', title : 'Fermes', icon : 'grass', class : ''},
    {path: '/getGreenHouse', title : 'Serres', icon : 'gite', class : ''},
    { path: '/devices', title: 'Appareils',  icon:'memory', class: '' },
    { path: '/sensors', title: 'Capteurs',  icon:'sensors', class: '' },
    { path: '/actuators', title: 'Actionneurs',  icon:'precision_manufacturing', class: '' },
    {path : '/planification', title : 'Planifications', icon : 'calendar_month', class: ''}
    
];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  menuItems: any[];
  role : string ='';

  constructor(public authService: AuthService) { }

  ngOnInit() {
    this.role = this.authService.CurrentUser.role;
    this.menuItems = ROUTES.filter(item => {
    if (item.title === 'Utilisateurs' && this.role !== 'Admin') {
      return false;
    }
    return true;
  });
  }
  isMobileMenu() {
      if ($(window).width() > 991) {
          return false;
      }
      return true;
  };
}
