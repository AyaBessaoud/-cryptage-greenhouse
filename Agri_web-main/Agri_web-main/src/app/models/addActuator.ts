import { endDevice } from './endDevice';
export interface addActuator{
    index : number,
    description : string,
    longt : number,
    lat : number,
    sensors : Array<SensorId>,
    output : string,
    device: DeviceId 
}

export interface SensorId {
    id: number
}

export interface DeviceId{
    id : number,
    codDevice : String
}