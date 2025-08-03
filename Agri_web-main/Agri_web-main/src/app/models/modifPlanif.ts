export interface ModifPlanification {
    actuatorId: ActuatorId,
    blocked : boolean,
    frame : string,
    creationDate : string,
    description : string
    pattern : string
    dates : Array<string>
    etat : number,
    times : Array<string>
    days : Array<string>
    months : Array<string>
}

interface ActuatorId {
    id: string
}