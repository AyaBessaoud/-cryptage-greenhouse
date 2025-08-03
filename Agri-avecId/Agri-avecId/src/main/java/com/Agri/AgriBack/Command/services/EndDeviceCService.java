package com.Agri.AgriBack.Command.services;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.GreenHouse;
import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.producer.EndDeviceProducer;
import com.Agri.AgriBack.Command.entity.endDevice;
import com.Agri.AgriBack.Command.repository.ActuatorCRepo;
import com.Agri.AgriBack.Command.repository.GreenHouseCRepo;
import com.Agri.AgriBack.Command.repository.SensorCRepo;
import com.Agri.AgriBack.Command.repository.endDeviceCRepo;
import com.Agri.AgriBack.DTO.EndDeviceDTO;
import com.Agri.AgriBack.Query.entity.ActuatorQ;
import com.Agri.AgriBack.Query.entity.SensorQ;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EndDeviceCService {

    @Autowired
    private EndDeviceProducer producer;

    @Autowired
    private endDeviceCRepo repo;
    @Autowired
    private SensorCRepo sensorRepo;
    @Autowired
    private ActuatorCRepo actuatorRepo;
    @Autowired
    private GreenHouseCRepo greenHouseRepo;

    public endDevice createEndDevice(endDevice device) {
        if (device.getSerre() != null) {
            GreenHouse serre = greenHouseRepo.findById(device.getSerre().getId())
                    .orElseThrow(() -> new RuntimeException("Serre not found"));
            device.setSerre(serre); // ⚠️ Associe l’objet réel
        }
        // 1. Sauvegarde initiale pour générer l'ID
        endDevice savedDevice = repo.save(device);

        // 2. Maintenant qu'on a l'ID, on peut construire le codDevice
        savedDevice.setCodDevice("A." + savedDevice.getId());

        // 3. Mise à jour avec le codDevice
        endDevice updatedDevice = repo.save(savedDevice);
        EndDeviceDTO dto = mapper(savedDevice);

        // 4. Maintenant on peut produire un seul événement complet
        producer.createEndDeviceEvent(dto); // envoie tout, codDevice y compris
        return savedDevice;
    }

    public endDevice updateEndDevice(endDevice device) {
        //charger la serre
        if (device.getSerre() != null) {
            GreenHouse serre = greenHouseRepo.findById(device.getSerre().getId())
                    .orElseThrow(() -> new RuntimeException("Serre not found"));
            device.setSerre(serre); //  Associe l’objet réel
        }
        // Charger les capteurs complets
        List<Sensor> sensors = device.getSensors().stream()
                .map(s -> sensorRepo.findById(s.getId()).orElseThrow(() -> new RuntimeException("Sensor not found")))
                .collect(Collectors.toList());

        // Charger les actionneurs complets
        List<Actuator> actuators = device.getLocalActuators().stream()
                .map(a -> actuatorRepo.findById(a.getId()).orElseThrow(() -> new RuntimeException("Actuator not found")))
                .collect(Collectors.toList());

        // Associer les objets complets
        device.setSensors(sensors);
        device.setLocalActuators(actuators);
        endDevice savedDevice = repo.save(device);
        EndDeviceDTO dto = mapper(savedDevice);
        producer.updateEndDeviceEvent(dto);
        return savedDevice;
    }

    public void deleteDevice(Long id) {
        if(repo.existsById(id)) {

            Optional<endDevice> optionalDevice = repo.findById(id);

            if (optionalDevice.isPresent()) {
                endDevice device = optionalDevice.get();

                if (device.getSerre() != null) {
                    GreenHouse serre = greenHouseRepo.findById(device.getSerre().getId())
                            .orElseThrow(() -> new RuntimeException("Serre not found"));
                     if(serre.getDevices() != null && !serre.getDevices().isEmpty()){
                         if(serre.getDevices().contains(device)){
                             serre.getDevices().remove(device);
                             greenHouseRepo.save(serre);
                         }
                     }
                }

                // 1. Supprimer les capteurs associés à ce device (dans la liste des capteurs)
                List<Sensor> sensors = device.getSensors();
                if (sensors != null && !sensors.isEmpty()) {
                    for (Sensor sensor : sensors) {
                        Optional<Sensor> optSensor = sensorRepo.findById(sensor.getId());
                        if (optSensor.isPresent()) {
                            Sensor updatedSensor = optSensor.get();
                            updatedSensor.setEndDevice(null);
                            sensorRepo.save(updatedSensor);
                        }
                    }
                }

                List<Actuator> actuators = device.getLocalOutput();
                if (actuators != null && !actuators.isEmpty()) {
                    for (Actuator actuator : actuators) {
                        Optional<Actuator> optActuator = actuatorRepo.findById(actuator.getId());
                        if (optActuator.isPresent()) {
                            Actuator existingAct = optActuator.get();
                            existingAct.setDevice(null);
                            actuatorRepo.save(existingAct);
                        }
                    }
                }

                repo.deleteById(id);
                producer.deleteEndDeviceEvent(id);
            }
        }
        else {
            throw new RuntimeException("L'appareil avec l'ID " + id + " n'existe pas.");
        }
    }

    public EndDeviceDTO mapper(endDevice device){
        EndDeviceDTO dto = new EndDeviceDTO();
        dto.setId(device.getId());
        dto.setLat(device.getConfig().getLat());
        dto.setLongt(device.getConfig().getLongt());
        dto.setPA(device.getConfig().getPA());
        dto.setPS(device.getConfig().getPS());
        dto.setCodDevice(device.getCodDevice());
        dto.setType(device.getType());
        dto.setNivBat(device.getNivBat());
        if(device.getSerre() != null){
            dto.setSerreId(device.getSerre().getId());
        }else{
            dto.setSerreId(null);
        }
        List<Long> ids = new ArrayList<>();
        if(device.getLocalActuators() != null && !device.getLocalActuators().isEmpty()){
            ids = device.getLocalActuators()
                    .stream()
                    .map(Actuator::getId)
                    .collect(Collectors.toList());
        }
        dto.setActuatorsIds(ids);
        List<Long> idSensors = new ArrayList<>();
        if(device.getSensors()!= null && !device.getSensors().isEmpty()){
            idSensors = device.getSensors()
                    .stream()
                    .map(Sensor::getId)
                    .collect(Collectors.toList());
        }
        dto.setSensorIds(idSensors);
        return dto;
    }
}
