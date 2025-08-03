import { NgModule } from '@angular/core';
import { CommonModule, } from '@angular/common';
import { BrowserModule  } from '@angular/platform-browser';
import { Routes, RouterModule } from '@angular/router';

import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { LoginComponent } from './login/login.component';

const routes: Routes =[
  {
    path: '',  //Correspond à l’URL racine (http://localhost:4200/
    redirectTo: 'login', //Redirige automatiquement vers le tableau de bord
    pathMatch: 'full',  //Assure que l’URL doit être exactement '' pour être redirigée
  }, {
    path: '',  // Définit un chemin vide pour englober toutes les routes enfants
    component: AdminLayoutComponent,  //Le composant parent qui sert de template principal
    children: [{
      path: '',  //Indique que AdminLayoutComponent contient d’autres routes.
      loadChildren: () => import('./layouts/admin-layout/admin-layout.module').then(m => m.AdminLayoutModule)
    }]
  },
  {
    path: 'login',
    component : LoginComponent
  }
];

@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    RouterModule.forRoot(routes,{
       useHash: true //Utilise un hash (#) dans l’URL Utile si le serveur ne supporte pas le mode history des navigateurs
    })
  ],
  exports: [
  ],
})
export class AppRoutingModule { }
