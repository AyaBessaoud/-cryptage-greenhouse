export interface ModifSensor{
          index : number,
          description : String,
          alertThersholdD : number,
          normalThersholdD : number,
          actuators : Array<ActuatorId>,
         typeSensor : string,
         endDevice : DeviceId,
         lat : number,
         longt : number,
         idU : string,
         alertThersholdN : number,
         normalThersholdN : number
}

export interface DeviceId {
      id : number
}

export interface ActuatorId {
    id: number
}