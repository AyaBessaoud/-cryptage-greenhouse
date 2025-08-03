import { Actuator } from "./Actuator"

export interface CreatePlanification {
    description : string
    pattern : string
    dates : Array<string>
    etat : number,
    times : Array<string>
    days : Array<string>
    months : Array<string>
    actuator : ActuatorId
}

interface ActuatorId {
    id: number
}