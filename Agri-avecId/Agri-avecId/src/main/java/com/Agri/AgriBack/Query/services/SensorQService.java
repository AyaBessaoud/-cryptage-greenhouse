package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.Query.entity.EmployeeQ;
import com.Agri.AgriBack.Query.entity.GreenHouseQ;
import com.Agri.AgriBack.Query.entity.SensorQ;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import com.Agri.AgriBack.Query.repository.EmployeeQRepo;
import com.Agri.AgriBack.Query.repository.SensorQRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SensorQService {

    @Autowired
    private SensorQRepo repo;
    @Autowired
    private EmployeeQRepo empRepo;

    public List<SensorQ> getAllSensors(){
        return repo.findAll();
    }

    public List<SensorQ> getSensorsByUser(String id){
        Optional<EmployeeQ> empOpt = empRepo.findById(id);
        if(empOpt.isPresent()) {
            EmployeeQ emp = empOpt.get();
            if (emp.getRole() == Employee.Role.Admin) {
                return repo.findAll();
            }else{
                List<GreenHouseQ> serres = emp.getSerre();
                List<SensorQ> userSensors  = new ArrayList<>();
                if(serres != null && !serres.isEmpty()){
                    for(GreenHouseQ serre : serres) {
                        if (serre.getDevices() != null && !serre.getDevices().isEmpty()) {
                            for (endDeviceQ deviceRef : serre.getDevices()) {
                                if(deviceRef.getSensors() != null && !deviceRef.getSensors().isEmpty()){
                                    for(SensorQ sensor : deviceRef.getSensors()){
                                        Optional<SensorQ> senOpt = repo.findById(sensor.getId());
                                        senOpt.ifPresent(userSensors::add);
                                    }
                                }
                            }
                        }
                    }
                    return userSensors;
                }
            }
        }
        return new ArrayList<>();
    }

    public SensorQ getSensorById( String id){
        Optional<SensorQ> sensor = repo.findById(id);
        return sensor.orElse(null);
    }
}
