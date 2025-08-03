package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.Query.entity.EmployeeQ;
import com.Agri.AgriBack.Query.entity.GreenHouseQ;
import com.Agri.AgriBack.Query.repository.EmployeeQRepo;
import com.Agri.AgriBack.Query.repository.GreenHouseQRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GreenHouseQService {
    @Autowired
    private GreenHouseQRepo repo;
    @Autowired
    private EmployeeQRepo empRepo;

    public List<GreenHouseQ> getAlls(String id){
        Optional<EmployeeQ> empOpt = empRepo.findById(id);
        if(empOpt.isPresent()){
            EmployeeQ emp = empOpt.get();
            if(emp.getRole() == Employee.Role.Admin){
                return repo.findAll();
            }else{
                List<GreenHouseQ> serres = emp.getSerre();
                if (serres == null || serres.isEmpty()) {
                    // Si null ou vide, on retourne liste vide (ou tu peux gérer autrement)
                    return new ArrayList<>();
                }
                List<GreenHouseQ> serresFromDB = new ArrayList<>();
                for(GreenHouseQ serre : serres){
                    if(serre != null && serre.getId() != null){
                        Optional<GreenHouseQ> serreOpt = repo.findById(serre.getId());
                        serreOpt.ifPresent(serresFromDB::add);
                    }
                }
                return serresFromDB;
            }
        }
        return new ArrayList<>(); // si l'employé n'existe pas
    }
}
