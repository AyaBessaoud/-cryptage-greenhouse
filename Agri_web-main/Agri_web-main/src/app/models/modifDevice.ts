import { DeviceConfig } from "./DeviceConfig"

export interface ModifDevice {
    type :string
    config : DeviceConfig,
    codDevice : String,
    nivBat : number,
    serre : serreId,
    localActuators: Array<actIds>,
    sensors : Array<sensorIds>,
}

interface serreId{
    id : number
}
interface actIds{
    id : number
}
interface sensorIds{
    id : number
}