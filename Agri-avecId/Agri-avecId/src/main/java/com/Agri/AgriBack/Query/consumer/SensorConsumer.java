package com.Agri.AgriBack.Query.consumer;

import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.entity.SensorType;
import com.Agri.AgriBack.Command.mapper.ActuatorCToQ;
import com.Agri.AgriBack.Command.mapper.SensorCToQ;
import com.Agri.AgriBack.DTO.SensorDTO;
import com.Agri.AgriBack.Query.entity.ActuatorQ;
import com.Agri.AgriBack.Query.entity.SensorConfig;
import com.Agri.AgriBack.Query.entity.SensorQ;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import com.Agri.AgriBack.Query.repository.ActuatorQRepo;
import com.Agri.AgriBack.Query.repository.EndDeviceQRepo;
import com.Agri.AgriBack.Query.repository.SensorConfigRepo;
import com.Agri.AgriBack.Query.repository.SensorQRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.Agri.AgriBack.Command.entity.Actuator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SensorConsumer {

    @Autowired
    private SensorQRepo repo;
    @Autowired
    private EndDeviceQRepo deviceRepo;
    @Autowired
    private ActuatorQRepo actRepo;
    @Autowired
    private ActuatorCToQ actMapper;
    @Autowired
    private SensorConfigRepo configRepo;

    @KafkaListener(topics = "Sensor_created", groupId = "Agri-group")
    public void consumeCreateEvent(SensorDTO sensor) {
        // Mapper le capteur
        SensorCToQ sensorMapper = new SensorCToQ();
        SensorQ sensorQ = sensorMapper.mapperDto(sensor);  // Le capteur avec l'EndDeviceQ déjà ajouté

        // Vérifier si le device associé au capteur existe déjà
        if (sensor.getDeviceId() != null) {
            // Récupérer le device (endDeviceQ) par son ID
            Optional<endDeviceQ> existingDeviceOpt = deviceRepo.findById(sensor.getDeviceId().toString());

            if (existingDeviceOpt.isPresent()) {
                endDeviceQ existingDevice = existingDeviceOpt.get();
                sensorQ.setEndDevice(existingDevice);
                // Ajouter le capteur à la liste des capteurs de l'endDevice
                List<SensorQ> sensors = existingDevice.getSensors();
                if (sensors == null) {
                    sensors = new ArrayList<>();
                }
                sensors.add(sensorQ);  // Ajouter le capteur à la liste
                existingDevice.setSensors(sensors);  // Mettre à jour la liste des capteurs dans l'appareil

                // Sauvegarder l'appareil mis à jour
                deviceRepo.save(existingDevice);
            } else {
                sensorQ.setEndDevice(null);
                // Si le device n'est pas trouvé, gérer l'erreur ici
                System.out.println("Device not found for ID: " + sensor.getDeviceId());
            }
        }

        // Remplir les actionneurs seulement si la liste est non nulle
        if (sensor.getActuatorsIds() != null) {
            List<ActuatorQ> acts = sensor.getActuatorsIds().stream()
                    .map(id -> actRepo.findById(id.toString()).orElse(null))
                    .filter(Objects::nonNull) // filtre les actionneurs non trouvés
                    .collect(Collectors.toList());
            sensorQ.setActuators(acts);
        } else {
            sensorQ.setActuators(new ArrayList<>()); // ou null si tu préfères
        }

        SensorConfig config = new SensorConfig();
        config.setIdU(sensorQ.getIdU());
        String codDevice = (sensorQ.getEndDevice() != null) ? sensorQ.getEndDevice().getCodDevice() : "null";
        config.setCodDevice((sensorQ.getEndDevice() != null) ? sensorQ.getEndDevice().getCodDevice() : null);

        String frame = buildSensorCommand(codDevice, sensorQ.getTypeSensor(), sensorQ.getIndex(), sensorQ.getAlertThersholdD(),
              sensorQ.getNormalThersholdD(), sensorQ.getAlertThersholdN(), sensorQ.getNormalThersholdN());

        config.setFrame(frame);
        configRepo.save(config);

        // Sauvegarder le capteur dans la base de données
        repo.save(sensorQ);
    }


    @KafkaListener(topics = "Sensor_deleted", groupId = "Agri-group")
    public void handleDeleteSensorEvent(Long sensorId) {
        // 1. Trouver le capteur dans la collection SensorQ
        Optional<SensorQ> sensorOpt = repo.findById(sensorId.toString());
        if (sensorOpt.isPresent()) {
            SensorQ sensor = sensorOpt.get();

            // 2. Récupérer l'EndDeviceQ associé au capteur
            endDeviceQ device = sensor.getEndDevice();  // Le capteur contient déjà l'EndDevice
            if (device != null) {
                // 3. Supprimer le capteur de la liste des capteurs de l'endDevice
                List<SensorQ> sensors = device.getSensors();
                if (sensors != null && sensors.contains(sensor)) {
                    sensors.remove(sensor);
                    device.setSensors(sensors);
                    deviceRepo.save(device); // Sauvegarder la modification dans EndDeviceQ
                    System.out.println("Capteur supprimé de la liste des capteurs de l'EndDevice. ID: " + sensorId);
                }
            }

            List<ActuatorQ> actuators = sensor.getActuators();
            if(actuators != null && !actuators.isEmpty()){
                for(ActuatorQ actuator : actuators){
                    if(actuator.getSensors() != null && !actuator.getSensors().isEmpty()){
                        if(actuator.getSensors().contains(sensor)){
                            actuator.getSensors().remove(sensor);
                            actRepo.save(actuator);
                        }
                    }
                }
            }

            // 4. Supprimer le capteur de la base de données SensorQ
            repo.deleteById(sensorId.toString());
            System.out.println("Capteur supprimé avec succès de la base de données. ID: " + sensorId);
        } else {
            System.out.println("Capteur non trouvé dans la base de données. ID: " + sensorId);
        }
    }



    @KafkaListener(topics = "Sensor_updated", groupId = "Agri-group")
    public void consumeUpdateEvent(SensorDTO sensor){
       String sensorId = sensor.getId().toString();

       // Supprimer le capteur des anciens endDeviceQ
       List<endDeviceQ> devices = deviceRepo.findAll();
       for (endDeviceQ device : devices) {
           List<SensorQ> sensors = device.getSensors();
           if (sensors != null) {
               sensors.removeIf(s -> s.getId().equals(sensorId));
               device.setSensors(sensors);
               deviceRepo.save(device);
           }
       }

       // Mapper le sensor mis à jour
       SensorCToQ sensorMapper = new SensorCToQ();
       SensorQ updatedSensorQ = sensorMapper.mapperDto(sensor);


        if(sensor.getActuatorsIds() != null && !sensor.getActuatorsIds().isEmpty()){
            List<ActuatorQ> acts = sensor.getActuatorsIds().stream()
                    .map(id -> actRepo.findById(id.toString()).orElse(null))
                    .filter(Objects::nonNull) // filtre les actionneurs non trouvés
                    .collect(Collectors.toList());
            updatedSensorQ.setActuators(acts);
        }
        else{
            updatedSensorQ.setActuators(new ArrayList<>());
        }


       // Associer à nouveau le capteur au bon endDeviceQ
       if (sensor.getDeviceId() != null) {
           String deviceId = sensor.getDeviceId().toString();
           Optional<endDeviceQ> optionalDevice = deviceRepo.findById(deviceId);
           if (optionalDevice.isPresent()) {
               endDeviceQ device = optionalDevice.get();
               List<SensorQ> sensors = device.getSensors();
               if (sensors == null) {
                   sensors = new ArrayList<>();
               }
               sensors.add(updatedSensorQ);
               device.setSensors(sensors);
               updatedSensorQ.setEndDevice(device);
               deviceRepo.save(device);
           }
       }else {
           updatedSensorQ.setEndDevice(null);
       }

        // Sauvegarder le capteur mis à jour
        repo.save(updatedSensorQ);

       Optional<SensorConfig> configOpt = configRepo.findByIdU(updatedSensorQ.getIdU());
       if(configOpt.isPresent()){
           SensorConfig config = configOpt.get();
           config.setIdU(updatedSensorQ.getIdU());
           String codDevice = (updatedSensorQ.getEndDevice() != null) ? updatedSensorQ.getEndDevice().getCodDevice() : "null";
           config.setCodDevice((updatedSensorQ.getEndDevice() != null) ? updatedSensorQ.getEndDevice().getCodDevice() : null);

           String frame = buildSensorCommand(codDevice, updatedSensorQ.getTypeSensor(), updatedSensorQ.getIndex(),updatedSensorQ.getAlertThersholdD(),
                   updatedSensorQ.getNormalThersholdD(), updatedSensorQ.getAlertThersholdN(), updatedSensorQ.getNormalThersholdN());

           config.setFrame(frame);
           configRepo.save(config);

       }

    }

    public String buildSensorCommand(String codDevice, SensorType typeSensor, int index, int alertThersholdD, int normalThersholdD, int alertThersholdN, int normalThersholdN) {
        String frame;
        frame =  String.format("3S;%s;%s;%d;%d;%d;%d;%d;", codDevice, typeSensor, index, alertThersholdD, normalThersholdD,alertThersholdN,normalThersholdN);
      return frame;
    }

}
