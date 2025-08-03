import { Component, OnInit, AfterViewInit  } from '@angular/core';
import { endDevice } from 'app/models/endDevice';
import { EnDeviceService } from 'app/services/en-device.service';

declare const google: any;

declare global {
  interface Window { initMap: () => void; }
}
@Component({
  selector: 'app-maps',
  templateUrl: './maps.component.html',
  styleUrls: ['./maps.component.css']
})
export class MapsComponent implements OnInit {

  constructor( public services : EnDeviceService){}

  ngAfterViewInit() {
    this.initMap();
  }

  ngOnInit() {
  }
  
  

  initMap() {
    const myLatlng = { lat: 36.8816580, lng: 10.3166711 }; // Coordonnées de la carte
    const mapOptions = {
      center: myLatlng,
      zoom: 13,
      scrollwheel: false,
      // Ajoutez d'autres options de carte si nécessaire
    };

    const map = new google.maps.Map(document.getElementById("map"), mapOptions);

    const marker = new google.maps.Marker({
      position: myLatlng,
      title: "Hello World!"
    });

    marker.setMap(map);
    
    // Ajout du champ de recherche pour l'autocomplétion
    const input = document.getElementById("pac-input") as HTMLInputElement;
    const autocomplete = new google.maps.places.Autocomplete(input);

    autocomplete.bindTo("bounds", map);

    // Écouteur d'événement pour gérer la sélection d'un lieu
    autocomplete.addListener("place_changed", () => {
      const place = autocomplete.getPlace();
      if (!place.geometry || !place.geometry.location) {
        console.log("Aucun détail disponible pour cet emplacement : " + place.name);
        return;
      }

      // Mise à jour de la carte et positionnement du marqueur
      map.setCenter(place.geometry.location);
      map.setZoom(17);

      new google.maps.Marker({
        position: place.geometry.location,
        map: map,
        title: place.name
      });
    });
  }

}