package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.entity.SensorType;
import com.Agri.AgriBack.Query.entity.*;
import com.Agri.AgriBack.Query.repository.EmployeeQRepo;
import com.Agri.AgriBack.Query.repository.EndDeviceQRepo;
import com.Agri.AgriBack.Query.repository.SensorQRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EndDeviceQService {

    @Autowired
    private EndDeviceQRepo repo;
    @Autowired
    private SensorQRepo sensorRepo;
    @Autowired
    private EmployeeQRepo empRepo;

    public List<endDeviceQ> getAllDevicesByUser(String id) {
        Optional<EmployeeQ> empOpt = empRepo.findById(id);
        if(empOpt.isPresent()) {
            EmployeeQ emp = empOpt.get();
            if (emp.getRole() == Employee.Role.Admin) {
                return repo.findAll();
            } else {
                List<GreenHouseQ> serres = emp.getSerre();
                List<endDeviceQ> userDevices  = new ArrayList<>();
                if(serres != null && !serres.isEmpty()){
                    for(GreenHouseQ serre : serres){
                        if(serre.getDevices() != null && !serre.getDevices().isEmpty()){
                            for (endDeviceQ deviceRef : serre.getDevices()) {
                                Optional<endDeviceQ> device = repo.findById(deviceRef.getId());
                                device.ifPresent(userDevices::add);
                            }
                        }
                    }
                    return userDevices;
                }
            }
        }
        return new ArrayList<>();
    }

    public List<endDeviceQ> getAllDevices (){
        return repo.findAll();
    }

    public endDeviceQ getDeviceById(String id){
        Optional<endDeviceQ> dev = repo.findById(id);
        return dev.orElse(null);
    }

    public SensorQ findSensor(int index, SensorType typeSensor, String codDevice){
        endDeviceQ device = repo.findByCodDevice(codDevice);
        if (device != null) {
            for (SensorQ sen : device.getSensors()) {
                if (sen.getIndex() == index && sen.getTypeSensor() == typeSensor) {
                    return sen; // Retourne directement le capteur trouvé
                }
            }
        }
        return null; // Aucun capteur trouvé
    }

    public ActuatorQ findActuator(int index , String codDevice, Actuator.Outputs output){
        endDeviceQ device = repo.findByCodDevice(codDevice);
        if(device != null){
            for(ActuatorQ act : device.getlocalActuators()){
                if(act.getIndex() == index && act.getOutput() == output){
                    return act;
                }
            }
        }
        return null;
    }

}
