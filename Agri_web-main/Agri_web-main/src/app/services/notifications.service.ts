import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Notification } from 'app/models/notification';
import { HttpClient } from '@angular/common/http';
import {baseUrl} from 'environments/environment';
import { Client, IMessage, Stomp } from '@stomp/stompjs';  //servent à gérer la connexion et les messages STOMP.
import * as SockJS from 'sockjs-client';  //crée une connexion WebSocket
declare var $: any;

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {

  constructor(private http : HttpClient) { 
    this.connect(); // Connexion au WebSocket dès que le service est initialisé
  }

  private notificationsSubject  = new BehaviorSubject<Notification[]>([]); // objet interne, utilisé pour pousser de nouvelles données
  notifications$ = this.notificationsSubject .asObservable(); // Observable pour écouter les notifications, exposé en lecture seule
  private stompClient: Client;  //gérer la connexion STOMP

  private connect(): void {
  const socket = new SockJS(baseUrl +'/notif'); //ouvres une connexion WebSocket vers ton backend Spring Boot à l'URL baseUrl + '/notif'
  this.stompClient = Stomp.over(() => socket); // crées une instance STOMP au-dessus de la connexion SockJS

  this.stompClient.onConnect = (frame) => { //frame contient des informations sur la session quand la connexion STOMP est établie avec succès
    console.log('WebSocket connecté :', frame);
    this.stompClient.subscribe('/topic/notifications', (message: IMessage) => { //lorsqu'un message est publié, la fonction callback est appelée avec ce message
      const notif = JSON.parse(message.body);

      const current = this.notificationsSubject.getValue();  //récupères les notifications actuelles
      if (!current.some(n => n.id === notif.id)) { //vérifies si la notification reçue existe déjà (basé sur son id) pour éviter les doublons.
        this.notificationsSubject.next([notif, ...current]);
      }
    });
  };

  this.stompClient.onStompError = (frame) => { //affiche un message d'erreur avec les détails.
    console.error('Erreur STOMP :', frame.headers['message'], frame.body);
  };

  this.stompClient.onWebSocketClose = () => {
    console.warn("WebSocket déconnecté, tentative de reconnexion dans 5s...");
    this.scheduleReconnect();
  };

  this.stompClient.onWebSocketError = (event) => {
    console.error("Erreur WebSocket : ", event);
    this.scheduleReconnect();
  };

  this.stompClient.activate(); //Lance la connexion STOMP
}

private reconnectTimeout: any;
private reconnectDelay = 5000; // 5 secondes
private scheduleReconnect() {
  if (this.reconnectTimeout) {
    clearTimeout(this.reconnectTimeout);
  }

  this.reconnectTimeout = setTimeout(() => {
    console.log("Tentative de reconnexion WebSocket...");
    this.connect();
  }, this.reconnectDelay);
}


    //récupérer les notifications d’un utilisateur, puis met à jour un BehaviorSubject
  getNotification(idUser : string) : void{
    this.http.get<Notification[]>(baseUrl+'/notification/user/'+ idUser).subscribe(
      (notifications) => this.notificationsSubject.next(notifications),
      (error) => console.error('Erreur lors de la récupération des notifications :', error)
    );
  }
  //permet de supprimer localement la notif du liste des notifs
  removeNotificationFromList(id: string): void {
  const current = this.notificationsSubject.getValue();
  const updated = current.filter(notif => notif.id !== id);
  this.notificationsSubject.next(updated);
}


  deleteNotif(id : string): Observable<any>{
    return this.http.delete(baseUrl+'/notification/'+ id);
  }

  clearNotifications() {
    this.notificationsSubject .next([]); // Réinitialise les notifications
  }

  /*addNotification(message: string) {  //pour des notifications locales, ajoutées côté client
    const currentNotifications = this.notificationsSubject .value; //récupère la liste actuelle des notifications
    this.notificationsSubject .next([...currentNotifications, message]);  //sert à mettre à jour la valeur de l’Observable
  }*/

  //toast
  showNotification(from, align, messages, color){
    const type = ['','info','success','warning','danger'];

    $.notify({
        icon: "notifications",
        message: messages

    },{
        type: type[color],
        timer: 4000,
        placement: {
            from: from,
            align: align
        },
        template: '<div data-notify="container" class="col-xl-4 col-lg-4 col-11 col-sm-4 col-md-4 alert alert-{0} alert-with-icon" role="alert">' +
          '<button mat-button  type="button" aria-hidden="true" class="close mat-button" data-notify="dismiss">  <i class="material-icons">close</i></button>' +
          '<i class="material-icons" data-notify="icon">notifications</i> ' +
          '<span data-notify="title">{1}</span> ' +
          '<span data-notify="message">{2}</span>' +
          '<div class="progress" data-notify="progressbar">' +
            '<div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>' +
          '</div>' +
          '<a href="{3}" target="{4}" data-notify="url"></a>' +
        '</div>'
    });
}
}
