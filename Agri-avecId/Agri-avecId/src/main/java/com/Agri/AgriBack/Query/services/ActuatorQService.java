package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.Query.entity.ActuatorQ;
import com.Agri.AgriBack.Query.entity.EmployeeQ;
import com.Agri.AgriBack.Query.entity.GreenHouseQ;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import com.Agri.AgriBack.Query.repository.ActuatorQRepo;
import com.Agri.AgriBack.Query.repository.EmployeeQRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActuatorQService {
    @Autowired
    private ActuatorQRepo repo;
    @Autowired
    private EmployeeQRepo empRepo;

    public List<ActuatorQ> getActuators(){
        return repo.findAll();
    }

    public List<ActuatorQ> getActuatorsByIdUser(String id){
        Optional<EmployeeQ> empOpt = empRepo.findById(id);
        if(empOpt.isPresent()) {
            EmployeeQ emp = empOpt.get();
            if (emp.getRole() == Employee.Role.Admin) {
                return repo.findAll();
            } else {
                List<GreenHouseQ> serres = emp.getSerre();
                if(serres != null && !serres.isEmpty()){
                    List<ActuatorQ> actUser = new ArrayList<>();
                    for(GreenHouseQ serre : serres) {
                        if (serre.getDevices() != null && !serre.getDevices().isEmpty()) {
                            for (endDeviceQ deviceRef : serre.getDevices()) {
                                if(deviceRef.getlocalActuators()!= null && !deviceRef.getlocalActuators().isEmpty()){
                                    for(ActuatorQ act : deviceRef.getlocalActuators()){
                                        Optional<ActuatorQ> actOpt = repo.findById(act.getId());
                                        actOpt.ifPresent(actUser::add);
                                    }
                                }
                            }
                        }
                    }
                    return actUser;
                }
            }
        }
        return new ArrayList<>();
    }

    public ActuatorQ getActuatorById(String id){
        Optional<ActuatorQ> OptAct = repo.findById(id);
        return OptAct.orElse(null);
    }

}
