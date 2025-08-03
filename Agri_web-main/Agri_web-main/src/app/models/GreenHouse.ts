import { endDevice } from "./endDevice";
import { Farm } from "./farm";

export interface GreenHouse{
    id : number,
    description : string,
    ferme : Farm
    devices : Array<endDevice>
}