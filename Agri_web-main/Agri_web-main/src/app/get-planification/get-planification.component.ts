import { Component } from '@angular/core';
import { PlanificationService } from 'app/services/planification.service';
import { Planification } from 'app/models/Planification';

@Component({
  selector: 'app-get-planification',
  templateUrl: './get-planification.component.html',
  styleUrls: ['./get-planification.component.css']
})
export class GetPlanificationComponent {
  constructor(private service: PlanificationService){}
  ListPlanifs : Planification[] = []

  ngOnInit(){
    this.getAllPlanifs()
  }

  getAllPlanifs(){
    this.service.getPlanification().subscribe(res=>{
      this.ListPlanifs = res;
      console.log("liste planif", this.ListPlanifs)
    })
  }
}
