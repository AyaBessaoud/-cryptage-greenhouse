package com.Agri.AgriBack.Query.consumer;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.mapper.ActuatorCToQ;
import com.Agri.AgriBack.Command.repository.ActuatorCRepo;
import com.Agri.AgriBack.DTO.ActuatorDTO;
import com.Agri.AgriBack.Query.entity.*;
import com.Agri.AgriBack.Query.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ActuatorConsumer {
    @Autowired
    private ActuatorQRepo repo;
    @Autowired
    private EndDeviceQRepo deviceRepo;
    @Autowired
    private ActuatorCToQ mapper;
    @Autowired
    private SensorQRepo sensorRepo;
    @Autowired
    private ActHistoryQRepo historyRepo;
    @Autowired
    private ActuatorConfigRepo configRepo;

    @KafkaListener(topics = "Actuator_created", groupId = "Agri-group")
    public void CreateActuator(ActuatorDTO actuator){

        //changement de type de Actuator à ActuatorQ

        ActuatorQ actuatorQ = mapper.toEntity(actuator);
        repo.save(actuatorQ);

        if(actuatorQ.getSensors() != null && !actuatorQ.getSensors().isEmpty()){
            List<SensorQ> sensorQS = actuatorQ.getSensors() ;
            for(SensorQ sen : sensorQS){
                Optional<SensorQ> existingSensorOpt = sensorRepo.findById(sen.getId());
                if(existingSensorOpt.isPresent()){
                    SensorQ sensor = existingSensorOpt.get();
                    if(sensor.getActuators() == null){
                        sensor.setActuators(new ArrayList<>());
                    }
                    if (sensor.getActuators().stream().noneMatch(a -> a.getId().equals(actuatorQ.getId()))) {
                        sensor.getActuators().add(actuatorQ);
                    }

                    sensorRepo.save(sensor);
                }
            }
        }

        if(actuatorQ.getDevice() != null){
            Optional<endDeviceQ> existingDeviceOpt = deviceRepo.findById(actuatorQ.getDevice().getId());
            if(existingDeviceOpt.isPresent()){
                endDeviceQ existingDevice = existingDeviceOpt.get();
                if (existingDevice.getlocalActuators() == null) {
                    existingDevice.setlocalActuators(new ArrayList<>());
                }
                if (existingDevice.getlocalActuators().stream().noneMatch(a -> a.getId().equals(actuatorQ.getId()))) {
                    existingDevice.getlocalActuators().add(actuatorQ);
                }
                deviceRepo.save(existingDevice);
            }
            else {
                // Si le device n'est pas trouvé, gérer l'erreur ici
                System.out.println("Device not found for ID: ");
            }
        }
        //Creation de l'historique
        ActuatorHistoryQ history = new ActuatorHistoryQ();
        history.setCodDevice((actuatorQ.getDevice() != null) ? actuatorQ.getDevice().getCodDevice() : null);
        history.setEtat(0);
        history.setIndex(actuator.getIndex());
        history.setIdU(actuator.getIdU());
        history.setOutput(actuatorQ.getOutput());
        history.setDate(LocalDateTime.now());
        historyRepo.save(history);

        //creation de l'objet configuration pour l'actionneur
        ActuatorConfig config = new ActuatorConfig();
        if (actuatorQ.getDevice() != null) {
            config.setCodDevice(actuatorQ.getDevice().getCodDevice());
        } else {
            config.setCodDevice(null); // ou une valeur par défaut si tu préfères
        }
        history.setIdU(actuator.getIdU());
        String codDevice = (actuatorQ.getDevice() != null) ? actuatorQ.getDevice().getCodDevice() : "null";
        config.setCodDevice((actuatorQ.getDevice() != null) ? actuatorQ.getDevice().getCodDevice() : null);
        String sensorIndexConcat;
        if (actuatorQ.getSensors() != null && !actuatorQ.getSensors().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (SensorQ sensor : actuatorQ.getSensors()) {
                sb.append(sensor.getTypeSensor())  // déjà une String
                        .append(";")
                        .append(sensor.getIndex())
                        .append(";");
            }
            sensorIndexConcat = sb.toString();

        } else {
            sensorIndexConcat = "null";
        }
        String frame = buildActuatorCommand(codDevice, actuatorQ.getOutput(), actuator.getIndex(), sensorIndexConcat);
        config.setIdU(actuator.getIdU());
        config.setFrame(frame);
        configRepo.save(config);
        System.out.println("Actionneur à ajouter");
    }

    @KafkaListener(topics = "Actuator_deleted", groupId = "Agri-group")
    public void DeleteActuatorEvent(Long id){
        String idStr = id.toString();
        Optional<ActuatorQ> existingAct = repo.findById(idStr);
        if(existingAct.isPresent()){
            ActuatorQ actuatorQ = existingAct.get();
            System.out.println("delete Event ");
            endDeviceQ device = actuatorQ.getDevice();
            if (device != null) {
                // 3. Supprimer le capteur de la liste des capteurs de l'endDevice
                List<ActuatorQ> actuators = device.getlocalActuators();
                if (actuators != null && actuators.contains(actuatorQ)) {
                    actuators.remove(actuatorQ);
                    device.setlocalActuators(actuators);
                    deviceRepo.save(device); // Sauvegarder la modification dans EndDeviceQ
                    System.out.println("Actionneur supprimé de la liste des capteurs de l'EndDevice. ID: " + id);
                }
            }

            List<SensorQ> sensor = actuatorQ.getSensors();
            if(sensor != null && !sensor.isEmpty()){
                for(SensorQ sen : sensor){
                    List<ActuatorQ> actuators = sen.getActuators();
                    if(actuators != null && !actuators.isEmpty()){
                        actuators.remove(actuatorQ);
                    }
                    sen.setActuators(actuators);
                    sensorRepo.save(sen);
                }

            }

            repo.deleteById(actuatorQ.getId());
            System.out.println("done ");
            System.out.println("Capteur supprimé avec succès de la base de données");
        }
    }

    public String buildActuatorCommand(String codDevice, Actuator.Outputs output, int index, String sensorIndex) {
        return String.format("3A;%s;%s;%d;%s", codDevice, output, index, sensorIndex);
    }

    @KafkaListener(topics = "Actuator_updated", groupId = "Agri-group")
    public void UpdateActuatorEvent(ActuatorDTO actuator){
        // Supprimer l'actionneur des anciens endDeviceQ
        List<endDeviceQ> devices = deviceRepo.findAll();
        for (endDeviceQ device : devices) {
            List<ActuatorQ> actuators = device.getlocalActuators();
            if (actuators != null) {
                actuators.removeIf(s -> s != null && s.getId().equals(actuator.getId().toString()));
                device.setlocalActuators(actuators);
                deviceRepo.save(device);
            }
        }
        //suprrime l'actionneur des anciens capteurs
        List<SensorQ> sensors = sensorRepo.findAll();
        for(SensorQ sensor : sensors){
            List<ActuatorQ> acts = sensor.getActuators();
            if(acts != null && !acts.isEmpty()){
                acts.removeIf(s -> s.getId().equals(actuator.getId().toString()));
                sensor.setActuators(acts);
                sensorRepo.save(sensor);
            }
        }

        //changement de type de Actuator à ActuatorQ
        //ActuatorQ actuatorQ = mapper.mapperActuator(actuator);
        ActuatorQ actuatorQ = mapper.toEntity(actuator);

        Optional<ActuatorConfig> configOpt = configRepo.findByIdU(actuatorQ.getIdU());
        if(configOpt.isPresent()){
            ActuatorConfig config = configOpt.get();
            actuator.setIdU(actuator.getIdU());
            String codDevice = (actuatorQ.getDevice() != null) ? actuatorQ.getDevice().getCodDevice() : "null";
            config.setCodDevice((actuatorQ.getDevice() != null) ? actuatorQ.getDevice().getCodDevice() : null);
            String sensorIndexConcat;
            if (actuatorQ.getSensors() != null && !actuatorQ.getSensors().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (SensorQ sensor : actuatorQ.getSensors()) {
                    sb.append(sensor.getTypeSensor())  // déjà une String
                            .append(";")
                            .append(sensor.getIndex())
                            .append(";");
                }
                sensorIndexConcat = sb.toString();

            } else {
                sensorIndexConcat = "null";
            }


            String frame = buildActuatorCommand(codDevice, actuatorQ.getOutput(), actuator.getIndex(), sensorIndexConcat);

            config.setFrame(frame);
            configRepo.save(config);
        }


        repo.save(actuatorQ);

        //on associe l'actionneur à l'end device
        if(actuatorQ.getDevice() != null){
            Optional<endDeviceQ> existingDeviceOpt = deviceRepo.findById(actuatorQ.getDevice().getId());
            if(existingDeviceOpt.isPresent()){
                endDeviceQ existingDevice = existingDeviceOpt.get();
                if (existingDevice.getlocalActuators() == null) {
                    existingDevice.setlocalActuators(new ArrayList<>());
                }
                existingDevice.getlocalActuators().add(actuatorQ);
                deviceRepo.save(existingDevice);
            }
        }

        //on associe l'actionneur au capteur
        if(actuatorQ.getSensors() != null && !actuatorQ.getSensors().isEmpty()){
            for(SensorQ sen : actuatorQ.getSensors()){
                Optional<SensorQ> sensorOpt = sensorRepo.findById(sen.getId());
                if(sensorOpt.isPresent()){
                    SensorQ sensor = sensorOpt.get();
                    if(sensor.getActuators() == null){
                        sensor.setActuators(new ArrayList<>());
                    }
                    sensor.getActuators().add(actuatorQ);
                    sensorRepo.save(sensor);
                }
            }

        }
    }
}
