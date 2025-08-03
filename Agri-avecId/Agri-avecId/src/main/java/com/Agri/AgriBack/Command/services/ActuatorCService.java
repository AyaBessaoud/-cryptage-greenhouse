package com.Agri.AgriBack.Command.services;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.entity.endDevice;
import com.Agri.AgriBack.Command.producer.ActuatorProducer;
import com.Agri.AgriBack.Command.repository.ActuatorCRepo;
import com.Agri.AgriBack.Command.repository.SensorCRepo;
import com.Agri.AgriBack.Command.repository.endDeviceCRepo;
import com.Agri.AgriBack.DTO.ActuatorDTO;
import com.Agri.AgriBack.Query.entity.ActuatorQ;
import com.Agri.AgriBack.Query.entity.SensorQ;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActuatorCService {

    @Autowired
    private ActuatorProducer producer;
    @Autowired
    private ActuatorCRepo repo;
    @Autowired
    private SensorCRepo sensorRepo;
    @Autowired
    private endDeviceCRepo deviceRepo;

    public Actuator createActuator(Actuator actuator) {
        if(actuator.getDevice() != null && actuator.getDevice().getId() != null){
            endDevice device = deviceRepo.findById(actuator.getDevice().getId()).orElseThrow();
        } else {
            actuator.setDevice(null);
        }

        List<Long> sensorIds = actuator.getSensors().stream()
                .map(Sensor::getId)
                .collect(Collectors.toList());

        List<Sensor> existingSensors = sensorRepo.findAllById(sensorIds);

        // Réassigner uniquement les capteurs déjà présents en base
        actuator.setSensors(existingSensors);
        // Étape 1 : sauvegarde initiale pour avoir l’ID
        Actuator savedActuator = repo.save(actuator);

        // Étape 2 : générer idU selon le type d’output
        String prefix = getPrefixForOutput(savedActuator.getOutput());
        savedActuator.setIdU(prefix + savedActuator.getId());

        // Étape 3 : mise à jour avec idU
        Actuator updatedActuator = repo.save(savedActuator);

        // Étape 4 : envoyer l’événement Kafka
        ActuatorDTO dto = mapper(updatedActuator);
        producer.createActuatorEvent(dto);
        return updatedActuator;
    }

    public Actuator updateActuator(Actuator actuator) {
        if (actuator.getSensors() != null && !actuator.getSensors().isEmpty()) {
            List<Sensor> sensors = new ArrayList<>();
            for(Sensor sen : actuator.getSensors()){
                Sensor sensor = sensorRepo.findById(sen.getId()).orElseThrow(() -> new EntityNotFoundException("Sensor not found with ID: " + sen.getId()));
                sensors.add(sensor);
            }
            actuator.setSensors(sensors);

        } else {
            actuator.setSensors(null); // ou garder comme il est si autorisé
        }
        if(actuator.getDevice() != null && actuator.getDevice().getId() != null){
            endDevice device = deviceRepo.findById(actuator.getDevice().getId()).orElseThrow();
        } else {
            actuator.setDevice(null);
        }
        Actuator savedActuator = repo.save(actuator);
        ActuatorDTO dto = mapper(savedActuator);
        producer.updateActuatorEvent(dto);
        return savedActuator;
    }

    public void deleteActuator(Long id) {
        Optional<Actuator> existingAct = repo.findById(id);
        if(existingAct.isPresent()){
            Actuator actuatorQ = existingAct.get();

            endDevice device = actuatorQ.getDevice();
            if (device != null) {
                // 3. Supprimer le capteur de la liste des capteurs de l'endDevice
                List<Actuator> actuators = device.getLocalOutput();
                if (actuators != null && actuators.contains(actuatorQ)) {
                    actuators.remove(actuatorQ);
                    device.setLocalOutput(actuators);
                    deviceRepo.save(device); // Sauvegarder la modification dans EndDeviceQ
                    System.out.println("Actionneur supprimé de la liste des capteurs de l'EndDevice. ID: " + id);
                }
            }

            List<Sensor> sensors = actuatorQ.getSensors();
            if (sensors != null && !sensors.isEmpty()) {
                for (Sensor sen : sensors) {
                    List<Actuator> actuators = sen.getActuators();
                    if (actuators != null && actuators.contains(actuatorQ)) {
                        actuators.remove(actuatorQ);
                        sensorRepo.save(sen); // mise à jour du capteur après suppression
                    }
                }
            }


            repo.deleteById(actuatorQ.getId());
            producer.deleteActuatorEvent(id);
            System.out.println("Actionneur supprimé avec succès de la base de données");
        }
    }

    private String getPrefixForOutput(Actuator.Outputs output) {
        switch (output) {
            case Lampe: return "AL.";
            case Ventilateur: return "AV.";
            case Pompe: return "AP.";
            default: return "A."; // fallback en cas d’erreur
        }
    }

    public ActuatorDTO mapper(Actuator updatedActuator){
        ActuatorDTO dto = new ActuatorDTO();
        dto.setDescription(updatedActuator.getDescription());
        dto.setId(updatedActuator.getId());
        dto.setIdU(updatedActuator.getIdU());
        dto.setIndex(updatedActuator.getIndex());
        dto.setLat(updatedActuator.getLat());
        dto.setLongt(updatedActuator.getLongt());
        if(updatedActuator.getDevice() != null){
            dto.setDeviceId(updatedActuator.getDevice().getId());
        } else {
            dto.setDeviceId(null);
        }

        dto.setOutput(updatedActuator.getOutput());
        List<Long> sensorIds = updatedActuator.getSensors()
                .stream()
                .map(Sensor::getId)
                .collect(Collectors.toList());

        dto.setSensorsIds(sensorIds);
        return dto;
    }

}
