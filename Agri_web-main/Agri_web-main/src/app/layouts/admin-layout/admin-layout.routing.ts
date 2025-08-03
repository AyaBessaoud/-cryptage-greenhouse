import { Routes } from '@angular/router';

import { DashboardComponent } from '../../dashboard/dashboard.component';
import { UserProfileComponent } from '../../user-profile/user-profile.component';
import { TableListComponent } from '../../table-list/table-list.component';
import { TypographyComponent } from '../../typography/typography.component';
import { MapsComponent } from '../../maps/maps.component';
import { NotificationsComponent } from '../../notifications/notifications.component';
import { AddUserComponent } from 'app/add-user/add-user.component';
import { GetUserComponent } from 'app/get-user/get-user.component';
import { AddDeviceComponent } from 'app/add-device/add-device.component';
import { GetDeviceComponent } from 'app/get-device/get-device.component';
import { ModifDeviceComponent } from '../../modif-device/modif-device.component';
import { AddSensorComponent } from 'app/add-sensor/add-sensor.component';
import { GettSensorComponent } from 'app/gett-sensor/gett-sensor.component';
import { ModifSensorComponent } from 'app/modif-sensor/modif-sensor.component';
import { AddActuatorComponent } from 'app/add-actuator/add-actuator.component';
import { GetActuatorComponent } from 'app/get-actuator/get-actuator.component';
import { ModifActuatorComponent } from 'app/modif-actuator/modif-actuator.component';
import { ViewActuatorComponent } from 'app/view-actuator/view-actuator.component';
import { ViewSensorComponent } from 'app/view-sensor/view-sensor.component';
import { ViewDeviceComponent } from 'app/view-device/view-device.component';
import { GetFarmComponent } from 'app/get-farm/get-farm.component';
import { GreenHouseComponent } from 'app/green-house/green-house.component';
import { DashboarddComponent } from 'app/dashboardd/dashboardd.component';
import { GetPlanificationComponent } from 'app/get-planification/get-planification.component';
import { AddPlanificationComponent } from 'app/add-planification/add-planification.component';
import { ModifPlanificationComponent } from 'app/modif-planification/modif-planification.component';

export const AdminLayoutRoutes: Routes = [
    { path: 'dash',      component: DashboardComponent },
    { path: 'user-profile',   component: UserProfileComponent },
    { path: 'table-list',     component: TableListComponent },
    { path: 'typography',     component: TypographyComponent },
    { path: 'maps',           component: MapsComponent },
    { path: 'notifications',  component: NotificationsComponent },
    {path: 'users/addUser', component : AddUserComponent},
    {path: 'users', component: GetUserComponent},
    {path: 'devices/addDevice', component: AddDeviceComponent},
    {path: 'devices', component: GetDeviceComponent},
    {path : 'devices/modifDevice/:id', component : ModifDeviceComponent},
    {path : 'devices/viewDevice/:id', component : ViewDeviceComponent},
    {path : 'sensors/addSensor', component : AddSensorComponent},
    {path : 'sensors', component : GettSensorComponent},
    {path : 'sensors/modifSensor/:id', component : ModifSensorComponent},
    {path : 'sensors/viewSensor/:id', component : ViewSensorComponent},
    {path : 'actuators/addActuator', component : AddActuatorComponent},
    {path : 'actuators', component : GetActuatorComponent},
    {path : 'actuators/modifAct/:id', component : ModifActuatorComponent},
    {path : 'actuators/viewActuator/:id', component : ViewActuatorComponent},
    {path: 'getFarm', component : GetFarmComponent},
    {path: 'getGreenHouse', component: GreenHouseComponent},
    {path: 'dashboard', component: DashboarddComponent},
    {path : 'planification', component: GetPlanificationComponent},
    {path : 'planification/addPlanif', component: AddPlanificationComponent},
    {path :'planification/modifPlanif/:id', component : ModifPlanificationComponent}
];
