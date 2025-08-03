import { endDevice } from "./endDevice";
import { Actuator } from "./Actuator";
export interface Sensor {
      id : String,
      index : number,
      description : String,
      alertThersholdD : number,
      normalThersholdD : number,
      actuators : Array<Actuator>,
     typeSensor : string,
     endDevice : endDevice,
     lat : number,
     longt : number,
     idU : string,
     alertThersholdN : number,
     normalThersholdN : number

}