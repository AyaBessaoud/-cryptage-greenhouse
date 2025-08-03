import { endDevice } from './endDevice';
import { DeviceConfig } from "./DeviceConfig"
export interface modifActuator{
    id : string
    index : number,
    description : String,
    longt : number,
    lat : number,
    sensors : Array<SensorId>,
    output : String,
    device: Device ,
    idU : string
 
}

export interface SensorId {
    id: String
}

export interface Device {
    id : String,
    config: DeviceConfig

}