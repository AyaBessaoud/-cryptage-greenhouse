import { Employee } from "./employee";

export interface Notification{
    id : string,
    users : Array<Employee>,
    message : string
}