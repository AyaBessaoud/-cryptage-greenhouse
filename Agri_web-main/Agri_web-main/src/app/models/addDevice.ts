import { DeviceConfig } from "./DeviceConfig"

export interface addDevice {
    type :string
    config : DeviceConfig,
    serre : serreId
}

interface serreId{
    id : number
}

