package com.Agri.AgriBack.Command.mapper;

import com.Agri.AgriBack.Command.entity.*;
import com.Agri.AgriBack.Command.entity.DeviceConfig;
import com.Agri.AgriBack.DTO.EndDeviceDTO;
import com.Agri.AgriBack.Query.entity.*;
import com.Agri.AgriBack.Query.repository.ActuatorQRepo;
import com.Agri.AgriBack.Query.repository.GreenHouseQRepo;
import com.Agri.AgriBack.Query.repository.SensorQRepo;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
public class EndDeviceCToQ {
    @Autowired
    private SensorQRepo sensorRepo;
    @Autowired
    private ActuatorQRepo actRepo;
    @Autowired
    private GreenHouseQRepo greenHouseRepo;

    public endDeviceQ mapperDevice(endDevice dev) {
        if (dev == null) return null;

        endDeviceQ deviceQ = new endDeviceQ();
        deviceQ.setId(dev.getId().toString());
        deviceQ.setCodDevice(dev.getCodDevice());
        deviceQ.setNivBat(dev.getNivBat());
        deviceQ.setConfig(mapperConfig(dev.getConfig()));
        deviceQ.setType(dev.getType());

        List<SensorQ> sensorsQ = new ArrayList<>();
        if (dev.getSensors() != null) {
            for (Sensor sensor : dev.getSensors()) {
                SensorQ sq = mapperSensor(sensor);
                //sq.setEndDevice(deviceQ); // Important pour la relation bidirectionnelle
                sensorsQ.add(sq);
            }
        }

        deviceQ.setSensors(sensorsQ);

        return deviceQ;
    }

    private SensorQ mapperSensor(Sensor sensor) {
        if (sensor == null) return null;

        SensorQ sq = new SensorQ();
        sq.setId(sensor.getId().toString());
        sq.setIndex(sensor.getIndex());
        sq.setDescription(sensor.getDescription());
        sq.setAlertThersholdD(sensor.getAlertThersholdD());
        sq.setNormalThersholdD(sensor.getNormalThersholdD());
        sq.setAlertThersholdN(sensor.getAlertThersholdN());
        sq.setNormalThersholdN(sensor.getNormalThersholdN());
        sq.setTypeSensor(sensor.getTypeSensor());

        return sq;
    }

    private DeviceConfigQ mapperConfig(DeviceConfig config){
        DeviceConfigQ cf = new DeviceConfigQ();
        cf.setLat(config.getLat());
        cf.setLongt(config.getLongt());
        cf.setPA(config.getPA());
        cf.setPS(config.getPS());
        return cf;
    }

    public endDeviceQ mapperDto(EndDeviceDTO dto){
        DeviceConfigQ config = new DeviceConfigQ();
        config.setPS(dto.getPS());
        config.setPA(dto.getPA());
        config.setLongt(dto.getLongt());
        config.setLat(dto.getLat());

        endDeviceQ device = new endDeviceQ();
        device.setId(dto.getId().toString());
        device.setConfig(config);
        device.setCodDevice(dto.getCodDevice());
        device.setType(dto.getType());
        device.setNivBat(dto.getNivBat());
        if(dto.getSerreId() != null){
            Optional<GreenHouseQ> serreOpt = greenHouseRepo.findById(dto.getSerreId().toString());
            if(serreOpt.isPresent()){
                GreenHouseQ serre = serreOpt.get();
                device.setSerre(serre);
            }
        }else{
            device.setSerre(null);
        }
        if(dto.getActuatorsIds() != null && !dto.getActuatorsIds().isEmpty()){
            List<ActuatorQ> acts = dto.getActuatorsIds().stream()
                    .map(id -> actRepo.findById(id.toString()).orElse(null))
                    .filter(Objects::nonNull) // filtre les actionneurs non trouvés
                    .collect(Collectors.toList());
            device.setlocalActuators(acts);
        }else{
            device.setlocalActuators(null);
        }
        if(dto.getSensorIds() != null && !dto.getSensorIds().isEmpty()){
            List<SensorQ> sensors = dto.getSensorIds().stream()
                    .map(id -> sensorRepo.findById(id.toString()).orElse(null))
                    .filter(Objects::nonNull) // filtre les actionneurs non trouvés
                    .collect(Collectors.toList());
            device.setSensors(sensors);
        }else{
            device.setSensors(null);
        }
        return device;
    }

}
