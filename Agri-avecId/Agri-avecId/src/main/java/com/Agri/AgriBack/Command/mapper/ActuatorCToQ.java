package com.Agri.AgriBack.Command.mapper;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.DeviceConfig;
import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.entity.endDevice;
import com.Agri.AgriBack.DTO.ActuatorDTO;
import com.Agri.AgriBack.Query.entity.ActuatorQ;
import com.Agri.AgriBack.Query.entity.DeviceConfigQ;
import com.Agri.AgriBack.Query.entity.SensorQ;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import com.Agri.AgriBack.Query.repository.EndDeviceQRepo;
import com.Agri.AgriBack.Query.repository.SensorQRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ActuatorCToQ {

    @Autowired
    private EndDeviceCToQ  endDeviceCToQ;
    @Autowired
    private SensorCToQ sensorCToQ;
    @Autowired
    private EndDeviceQRepo deviceRepo;
    @Autowired
    private SensorQRepo sensorRepo;
    public ActuatorQ mapperActuator(Actuator actuator) {
        if (actuator == null) return null;

        ActuatorQ oq = new ActuatorQ();
        oq.setId(actuator.getId().toString());
        oq.setIndex(actuator.getIndex());
        oq.setDescription(actuator.getDescription());
        oq.setOutput(actuator.getOutput());
        oq.setLat(actuator.getLat());
        oq.setLongt(actuator.getLongt());
        oq.setIdU(actuator.getIdU());

        if(actuator.getSensors() != null && !actuator.getSensors().isEmpty()){
            List<SensorQ> sensors = new ArrayList<>();
            for(Sensor sensor : actuator.getSensors()){
                SensorQ sensorQ = mapperSensor(sensor);
                sensors.add(sensorQ);
            }
            oq.setSensors(sensors);
        }
        else{
            oq.setSensors(null);
        }

        if (actuator.getDevice() != null) {
            endDevice device = actuator.getDevice();
            endDeviceQ deviceQ = endDeviceCToQ.mapperDevice(device);

            oq.setDevice(deviceQ); // Liaison côté ActuatorQ
        }
        else {
            oq.setDevice(null);
        }
        return oq;
    }

    public endDeviceQ mapperDevice(endDevice dev) {
        if (dev == null) return null;

        endDeviceQ deviceQ = new endDeviceQ();
        deviceQ.setId(dev.getId().toString());
        deviceQ.setCodDevice(dev.getCodDevice());
        deviceQ.setNivBat(dev.getNivBat());
        deviceQ.setConfig(mapperConfig(dev.getConfig()));
        deviceQ.setType(dev.getType());
        return deviceQ;
    }

    private DeviceConfigQ mapperConfig(DeviceConfig config){
        DeviceConfigQ cf = new DeviceConfigQ();
        cf.setLat(config.getLat());
        cf.setLongt(config.getLongt());
        cf.setPA(config.getPA());
        cf.setPS(config.getPS());
        return cf;
    }

    private SensorQ mapperSensor(Sensor sensor)  {
        SensorQ sensorQ = new SensorQ();
        sensorQ.setId(sensor.getId().toString());
        sensorQ.setIndex(sensor.getIndex());
        sensorQ.setDescription(sensor.getDescription());
        sensorQ.setAlertThersholdD(sensor.getAlertThersholdD());
        sensorQ.setNormalThersholdD(sensor.getNormalThersholdD());
        sensorQ.setTypeSensor(sensor.getTypeSensor());
        sensorQ.setLat(sensor.getLat());
        sensorQ.setLongt(sensor.getLongt());
        sensorQ.setIdU(sensor.getIdU());
        sensorQ.setAlertThersholdN(sensor.getAlertThersholdN());
        sensorQ.setNormalThersholdN(sensor.getNormalThersholdN());
        return sensorQ;
    }

    public ActuatorQ toEntity(ActuatorDTO dto) {
        ActuatorQ actuator = new ActuatorQ();
        actuator.setId(dto.getId().toString());
        actuator.setIdU(dto.getIdU());
        actuator.setIndex(dto.getIndex());
        actuator.setDescription(dto.getDescription());
        actuator.setLat(dto.getLat());
        actuator.setLongt(dto.getLongt());
        actuator.setOutput(dto.getOutput());

        // Remplir les sensors seulement si la liste est non nulle
        if (dto.getSensorsIds() != null) {
            List<SensorQ> sensors = dto.getSensorsIds().stream()
                    .map(id -> sensorRepo.findById(id.toString()).orElse(null))
                    .filter(Objects::nonNull) // filtre les sensors non trouvés
                    .collect(Collectors.toList());
            actuator.setSensors(sensors);
        } else {
            actuator.setSensors(new ArrayList<>()); // ou null si tu préfères
        }

        // Remplir le device seulement s'il est présent et trouvé
        if (dto.getDeviceId() != null) {
            deviceRepo.findById(dto.getDeviceId().toString())
                    .ifPresent(actuator::setDevice);
        } else {
            actuator.setDevice(null); // explicitement
        }

        return actuator;
    }


}
