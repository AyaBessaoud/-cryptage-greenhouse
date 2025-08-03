import { GreenHouse } from "./GreenHouse";

export interface Farm {
    id : number,
    description : string,
    serres : Array<GreenHouse>
}