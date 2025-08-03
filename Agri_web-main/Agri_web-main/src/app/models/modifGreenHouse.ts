export interface ModifGreenHouse {
    description : string,
    ferme : FarmId
    devices : Array<deviceIds>
}

export interface FarmId {
    id : number
}

export interface deviceIds {
    id : number
}