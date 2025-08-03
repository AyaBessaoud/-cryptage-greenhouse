import { Actuator } from "./Actuator"
export interface Planification {
    id : string,
    actuator : Actuator,
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
