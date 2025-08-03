import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { AppRoutingModule } from './app.routing';
import { ComponentsModule } from './components/components.module';
import { AppComponent } from './app.component';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { LoginComponent } from './login/login.component';
import { AddUserComponent } from './add-user/add-user.component';
import { GetUserComponent } from './get-user/get-user.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core'; // Pour la compatibilité native des dates (ex: Date)
import { BrowserModule } from '@angular/platform-browser';
import { AddDeviceComponent } from './add-device/add-device.component';
import { GetDeviceComponent } from './get-device/get-device.component';
import { AddSensorComponent } from './add-sensor/add-sensor.component';
import { ModifDeviceComponent } from './modif-device/modif-device.component';
import { GettSensorComponent } from './gett-sensor/gett-sensor.component';
import { ModifSensorComponent } from './modif-sensor/modif-sensor.component';
import { AddActuatorComponent } from './add-actuator/add-actuator.component';
import { GetActuatorComponent } from './get-actuator/get-actuator.component';
import { ModifActuatorComponent } from './modif-actuator/modif-actuator.component';
import { ViewDeviceComponent } from './view-device/view-device.component';
import { ViewSensorComponent } from './view-sensor/view-sensor.component';
import { ViewActuatorComponent } from './view-actuator/view-actuator.component';
import { GetFarmComponent } from './get-farm/get-farm.component';
import { GreenHouseComponent } from './green-house/green-house.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { DashboarddComponent } from './dashboardd/dashboardd.component';
import { NgChartsModule } from 'ng2-charts';
import { NgApexchartsModule } from 'ng-apexcharts';
import { CustomInterceptor } from './custom.interceptor';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { GetPlanificationComponent } from './get-planification/get-planification.component';
import { AddPlanificationComponent } from './add-planification/add-planification.component';
import { ModifPlanificationComponent } from './modif-planification/modif-planification.component';



@NgModule({
  imports: [
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    ComponentsModule,
    RouterModule,
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,  // Obligatoire pour Angular Material
    MatFormFieldModule,       // Ajout du module MatFormField
    MatInputModule,
    NgxPaginationModule, 
    NgChartsModule,
    NgApexchartsModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  declarations: [
    AppComponent,
    AdminLayoutComponent,
    LoginComponent,
    AddUserComponent,
    GetUserComponent,
    AddDeviceComponent,
    GetDeviceComponent,
    AddSensorComponent,
    ModifDeviceComponent,
    GettSensorComponent,
    ModifSensorComponent,
    AddActuatorComponent,
    GetActuatorComponent,
    ModifActuatorComponent,
    ViewDeviceComponent,
    ViewSensorComponent,
    ViewActuatorComponent,
    GetFarmComponent,
    GreenHouseComponent,
    DashboarddComponent,
    GetPlanificationComponent,
    AddPlanificationComponent,
    ModifPlanificationComponent,

  ],
  providers: [{provide: HTTP_INTERCEPTORS, useClass: CustomInterceptor,
    multi:true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
