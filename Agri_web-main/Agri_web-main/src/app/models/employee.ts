import { Farm } from "./farm";
import { GreenHouse } from "./GreenHouse";

export interface Employee {
    email: string,
    firstName: string,
    lastName: string,
    address: string,
    mobile: string,
    password : string,
    id : string,
    role: string,
    serre : Array<GreenHouse>,
    ferme : Array<Farm>
}