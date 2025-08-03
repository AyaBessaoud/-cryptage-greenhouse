package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.Query.entity.EmployeeQ;
import com.Agri.AgriBack.Query.entity.FarmQ;
import com.Agri.AgriBack.Query.repository.EmployeeQRepo;
import com.Agri.AgriBack.Query.repository.FarmQRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FarmQService {
    @Autowired
    private FarmQRepo repo;
    @Autowired
    private EmployeeQRepo empRepo;

    public List<FarmQ> getAllFarms(String id){
        Optional<EmployeeQ> empOpt = empRepo.findById(id);
        if(empOpt.isPresent()){
            EmployeeQ emp = empOpt.get();
            if(emp.getRole() == Employee.Role.Admin){
                return repo.findAll();
            }else{
                List<FarmQ> farms = emp.getFerme();

                if (farms == null || farms.isEmpty()) {
                    // Si null ou vide, on retourne liste vide (ou tu peux gérer autrement)
                    return new ArrayList<>();
                }

                List<FarmQ> farmsFromDb = new ArrayList<>();
                for (FarmQ farm : farms) {
                    if (farm != null && farm.getId() != null) {
                        Optional<FarmQ> farmFromDbOpt = repo.findById(farm.getId());
                        farmFromDbOpt.ifPresent(farmsFromDb::add);
                    }
                }
                return farmsFromDb;
            }

        }
        return new ArrayList<>(); // si l'employé n'existe pas
    }
}
