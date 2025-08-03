import { Sensor } from "./Sensor";
import { endDevice } from "./endDevice";
export interface Actuator{
    id: string,
    index : number,
    description : String,
    sensors : Array<Sensor>,
    device : endDevice,
    output : String,
    longt : number,
    lat : number,
    idU: string
}