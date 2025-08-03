export interface AddSensor {
      index : number,
      description : String,
      alertThersholdD : number,
      normalThersholdD : number,
      actuators : Array<ActuatorId>,
     typeSensor : String,
     endDevice : DeviceId,
     longt : number,
     lat :number,
     alertThersholdN : number,
     normalThersholdN : number
}

export interface DeviceId {
      id : number
}

export interface ActuatorId {
    id: number
}