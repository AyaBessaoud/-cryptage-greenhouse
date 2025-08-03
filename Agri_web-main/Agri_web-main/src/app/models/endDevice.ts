import { Sensor } from "./Sensor"
import { DeviceConfig } from "./DeviceConfig"
import { Actuator } from "./Actuator"
import { GreenHouse } from "./GreenHouse"
export interface endDevice {
      id : String,
      codDevice : String,
      nivBat : number,
      config: DeviceConfig,
      sensors : Array<Sensor>,
      localActuators : Array<Actuator>,
      type : string,
      serre : GreenHouse
}