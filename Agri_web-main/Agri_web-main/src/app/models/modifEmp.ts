export interface ModifEmployee {
    email: string,
    firstName: string,
    lastName: string,
    address: string,
    mobile: string,
    role: string,
    password : string,
    serre : Array<idSerres>,
    ferme : Array<idFermes>
}

interface idSerres {
    id : number
}
interface idFermes {
    id : number
}