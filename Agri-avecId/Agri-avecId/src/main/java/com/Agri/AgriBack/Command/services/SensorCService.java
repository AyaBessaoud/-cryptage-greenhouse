package com.Agri.AgriBack.Command.services;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.SensorType;
import com.Agri.AgriBack.Command.entity.endDevice;
import com.Agri.AgriBack.Command.producer.SensorProducer;
import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.repository.ActuatorCRepo;
import com.Agri.AgriBack.Command.repository.SensorCRepo;
import com.Agri.AgriBack.Command.repository.endDeviceCRepo;
import com.Agri.AgriBack.DTO.SensorDTO;
import com.Agri.AgriBack.Query.entity.ActuatorQ;
import com.Agri.AgriBack.Query.entity.SensorQ;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorCService {

    @Autowired
    private SensorCRepo repo;
    @Autowired
    private SensorProducer producer;
    @Autowired
    private endDeviceCRepo deviceRepo;
    @Autowired
    private ActuatorCRepo actuatorRepo;
    @Autowired
    private endDeviceCRepo endDeviceRepo;

    public Sensor CreateSensor(Sensor sensor) {
        // Étape 0 : Récupérer le endDevice depuis la base de données si présent
        if (sensor.getEndDevice() != null && sensor.getEndDevice().getId() != null) {
            endDevice realDevice = endDeviceRepo.findById(sensor.getEndDevice().getId())
                    .orElseThrow(() -> new RuntimeException("EndDevice with ID " + sensor.getEndDevice().getId() + " not found"));

            sensor.setEndDevice(realDevice); // Associer le vrai device
        }
        // Étape 1 : Enregistrement initial pour obtenir l'ID
        Sensor savedSensor = repo.save(sensor);

        // Étape 2 : Construction du idU en fonction du type de capteur
        String prefix = getPrefixForSensorType(savedSensor.getTypeSensor());
        savedSensor.setIdU(prefix + savedSensor.getId());

        // Étape 3 : Mise à jour dans la base avec idU défini
        Sensor updatedSensor = repo.save(savedSensor);
        SensorDTO dto = mapper(updatedSensor);
        // Étape 4 : Publier l’événement complet
        producer.createSensorEvent(dto);
        return updatedSensor;
    }

    public Sensor UpdateSensor(Sensor sensor) {
        // Récupérer les actionneurs depuis la base de données si présent
        List<Actuator> managedActuators = sensor.getActuators().stream()
                .map(actuator -> actuatorRepo.findById(actuator.getId())
                        .orElseThrow(() -> new RuntimeException("Actuator not found with id: " + actuator.getId())))
                .collect(Collectors.toList());
        sensor.setActuators(managedActuators);

        // Récupérer le endDevice depuis la base de données si présent
        if (sensor.getEndDevice() != null && sensor.getEndDevice().getId() != null) {
            endDevice realDevice = endDeviceRepo.findById(sensor.getEndDevice().getId())
                    .orElseThrow(() -> new RuntimeException("EndDevice with ID " + sensor.getEndDevice().getId() + " not found"));

            sensor.setEndDevice(realDevice); // Associer le vrai device
        }

        Sensor savedSensor = repo.save(sensor);
        SensorDTO dto = mapper(savedSensor);
        producer.updateSensorEvent(dto);
        return savedSensor;
    }

    public void deleteSensor(Long id) {
        Optional<Sensor> sensorOpt = repo.findById(id);
        if (sensorOpt.isPresent()) {
            Sensor sensor = sensorOpt.get();

            // 2. Récupérer l'EndDeviceQ associé au capteur
            endDevice device = sensor.getEndDevice();  // Le capteur contient déjà l'EndDevice
            if (device != null) {
                // 3. Supprimer le capteur de la liste des capteurs de l'endDevice
                List<Sensor> sensors = device.getSensors();
                if (sensors != null && sensors.contains(sensor)) {
                    sensors.remove(sensor);
                    device.setSensors(sensors);
                    deviceRepo.save(device); // Sauvegarder la modification dans EndDeviceQ
                    System.out.println("Capteur supprimé de la liste des capteurs de l'EndDevice. ID: " + id);
                }
            }

            List<Actuator> actuators = sensor.getActuators();
            if (actuators != null && !actuators.isEmpty()) {
                for (Actuator actuator : actuators) {
                    if(actuator.getSensors() != null && !actuator.getSensors().isEmpty()){
                        if(actuator.getSensors().contains(sensor)){
                            actuator.getSensors().remove(sensor);
                            actuatorRepo.save(actuator);
                        }
                    }
                }
            }

            // 4. Supprimer le capteur de la base de données SensorQ
            repo.deleteById(id);
            producer.deleteSensorEvent(id);
            System.out.println("Capteur supprimé avec succès de la base de données. ID: " + id);
        } else {
            System.out.println("Capteur non trouvé dans la base de données. ID: " + id);
        }
    }
    private String getPrefixForSensorType(SensorType typeSensor) {
        switch (typeSensor) {
            case gaz: return "CG.";
            case humidite: return "CH.";
            case temperature: return "CT.";
            case humSol: return "CHS.";
            case lumiere: return "CL.";
            default: return "C."; // fallback au cas où
        }
    }

    private SensorDTO mapper(Sensor sensor){
        SensorDTO dto = new SensorDTO();
        dto.setId(sensor.getId());
        dto.setIdU(sensor.getIdU());
        dto.setIndex(sensor.getIndex());
        dto.setDescription(sensor.getDescription());
        dto.setLat(sensor.getLat());
        dto.setLongt(sensor.getLongt());
        dto.setDeviceId(sensor.getEndDevice() != null ? sensor.getEndDevice().getId() : null);
        dto.setAlertThersholdD(sensor.getAlertThersholdD());
        dto.setNormalThersholdD(sensor.getNormalThersholdD());
        dto.setAlertThersholdN(sensor.getAlertThersholdN());
        dto.setTypeSensor(sensor.getTypeSensor());
        dto.setNormalThersholdN(sensor.getNormalThersholdN());
        List<Long> actuatorIds = sensor.getActuators()
                .stream()
                .map(Actuator::getId)
                .collect(Collectors.toList());
        dto.setActuatorsIds(actuatorIds);
        return dto;

    }

}

