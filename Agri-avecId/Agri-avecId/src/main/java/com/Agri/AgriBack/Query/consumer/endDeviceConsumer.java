package com.Agri.AgriBack.Query.consumer;

import com.Agri.AgriBack.Command.entity.GreenHouse;
import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.entity.endDevice;
import com.Agri.AgriBack.Command.mapper.EndDeviceCToQ;
import com.Agri.AgriBack.DTO.EndDeviceDTO;
import com.Agri.AgriBack.Query.entity.*;
import com.Agri.AgriBack.Query.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class endDeviceConsumer {

    @Autowired
    private EndDeviceQRepo repo;
    @Autowired
    private SensorQRepo sensorRepo;
    @Autowired
    private EndDeviceCToQ mapper;
    @Autowired
    private ActuatorQRepo actuatorRepo;
    @Autowired
    private DeviceConfigRepo configRepo;
    @Autowired
    private GreenHouseQRepo greenHouseRepo;

    @KafkaListener(topics = "EndDevice_created", groupId = "Agri-group")
    public void consumeCreateEvent(EndDeviceDTO device){
        System.out.println("post Event ");
        endDeviceQ endDeviceMapped = mapper.mapperDto(device);

        com.Agri.AgriBack.Query.entity.DeviceConfig config = new DeviceConfig();
        config.setCodDevice(endDeviceMapped.getCodDevice());
        String frame = buildDeviceCommand(endDeviceMapped.getCodDevice(),endDeviceMapped.getConfig().getPS(),endDeviceMapped.getConfig().getPA());
        config.setFrame(frame);
        configRepo.save(config);

        //ajout du device à la liste des devices de la serre
        if(device.getSerreId() != null) {
            Optional<GreenHouseQ> serreOpt = greenHouseRepo.findById(device.getSerreId().toString());
            if (serreOpt.isPresent()) {
                GreenHouseQ serre = serreOpt.get();
                List<endDeviceQ> devices = serre.getDevices();
                if(devices == null){
                    devices = new ArrayList<>();
                }
                devices.add(endDeviceMapped);
                serre.setDevices(devices);
                greenHouseRepo.save(serre);
            }
        }

        repo.save(endDeviceMapped);
    }

    @KafkaListener(topics = "EndDevice_updated", groupId = "Agri-group")
    public void consumeUpdateEvent(EndDeviceDTO device){
        String id = device.getId().toString();
        //supprime device de tous les serres
        List<GreenHouseQ> serres = greenHouseRepo.findAll();
        for(GreenHouseQ serre : serres){
            List<endDeviceQ> devices = serre.getDevices();
            if(devices != null && !devices.isEmpty()){
                devices.removeIf(s -> s.getId().equals(device.getId().toString()));
                serre.setDevices(devices);
                greenHouseRepo.save(serre);
            }
        }

        Optional<endDeviceQ> existingDevOpt = repo.findById(id);

        if (existingDevOpt.isPresent()) {
            // Re-mapper entièrement l'objet avec les nouvelles données
            endDeviceQ updatedDev = mapper.mapperDto(device);

            Optional<DeviceConfig> configOpt = configRepo.findByCodDevice(updatedDev.getCodDevice());
            if(configOpt.isPresent()){
                com.Agri.AgriBack.Query.entity.DeviceConfig config = configOpt.get();
                config.setCodDevice(updatedDev.getCodDevice());
                String frame = buildDeviceCommand(updatedDev.getCodDevice(),updatedDev.getConfig().getPS(),updatedDev.getConfig().getPA());
                config.setFrame(frame);
                configRepo.save(config);
            }

            //ajout du device à la liste des devices de la serre
            if(device.getSerreId() != null) {
                Optional<GreenHouseQ> serreOpt = greenHouseRepo.findById(device.getSerreId().toString());
                if (serreOpt.isPresent()) {
                    GreenHouseQ serre = serreOpt.get();
                    List<endDeviceQ> devices = serre.getDevices();
                    if(devices == null){
                        devices = new ArrayList<>();
                    }
                    devices.add(updatedDev);
                    serre.setDevices(devices);
                    greenHouseRepo.save(serre);
                }
            }

            repo.save(updatedDev);
        }

    }

    @KafkaListener(topics = "EndDevice_deleted", groupId = "Agri-group")
    public void handleDeleteEndDevice(Long deviceId) {
        String id = deviceId.toString();

        // Récupérer l'EndDeviceQ à partir de la base de données
        Optional<endDeviceQ> optionalDevice = repo.findById(id);

        if (optionalDevice.isPresent()) {
            endDeviceQ device = optionalDevice.get();

            // 1. Supprimer les capteurs associés à ce device (dans la liste des capteurs)
            List<SensorQ> sensors = device.getSensors();
            if (sensors != null && !sensors.isEmpty()) {
                for (SensorQ sensor : sensors){
                    Optional<SensorQ> optSensor =  sensorRepo.findById(sensor.getId());
                    if(optSensor.isPresent()){
                        SensorQ updatedSensor = optSensor.get();
                        updatedSensor.setEndDevice(null);
                        sensorRepo.save(updatedSensor);
                    }
                }
            }

            if (device.getSerre() != null) {
                GreenHouseQ serre = greenHouseRepo.findById(device.getSerre().getId())
                        .orElseThrow(() -> new RuntimeException("Serre not found"));
                if(serre.getDevices() != null && !serre.getDevices().isEmpty()){
                    if(serre.getDevices().contains(device)){
                        serre.getDevices().remove(device);
                        greenHouseRepo.save(serre);
                    }
                }
            }

            List<ActuatorQ> actuators = device.getlocalActuators();
            if(actuators != null && !actuators.isEmpty()){
                for(ActuatorQ actuator : actuators){
                    Optional<ActuatorQ> optActuator = actuatorRepo.findById(actuator.getId());
                    if(optActuator.isPresent()){
                        ActuatorQ existingAct = optActuator.get();
                        existingAct.setDevice(null);
                        actuatorRepo.save(existingAct);
                    }
                }
            }

            if(device.getSerre() != null){
                Optional<GreenHouseQ> serreOpt = greenHouseRepo.findById(device.getSerre().getId());
                if(serreOpt.isPresent()){
                    GreenHouseQ serre = serreOpt.get();
                    List<endDeviceQ> devices = serre.getDevices();
                    if(devices != null && !devices.isEmpty()){
                        devices.removeIf(s -> s.getId().equals(device.getId()));
                        serre.setDevices(devices);
                        greenHouseRepo.save(serre);
                    }
                }
            }

            // 2. Supprimer le device de la base de données
            repo.deleteById(id);

            System.out.println("EndDevice supprimé avec succès, ID: " + id);
        } else {
            System.out.println("EndDevice non trouvé dans la base de données, ID: " + id);
        }
    }

    public String buildDeviceCommand(String codDevice, int PS, int PA) {
        return String.format("3D;%s;%d;%d;", codDevice, PS, PA);
    }

}
