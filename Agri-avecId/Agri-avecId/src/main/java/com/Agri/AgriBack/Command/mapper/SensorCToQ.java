package com.Agri.AgriBack.Command.mapper;

import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.DTO.SensorDTO;
import com.Agri.AgriBack.Query.entity.SensorQ;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import com.Agri.AgriBack.Query.repository.EndDeviceQRepo;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@NoArgsConstructor
@Component
public class SensorCToQ {
    @Autowired
    private EndDeviceQRepo deviceRepo;
    public SensorQ mapperSensor(Sensor sensor) {
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

        // Si l'EndDevice existe, le mapper en endDeviceQ et l'ajouter à sensorQ
        if (sensor.getEndDevice() != null) {
            EndDeviceCToQ deviceMapper = new EndDeviceCToQ();
            endDeviceQ deviceQ = deviceMapper.mapperDevice(sensor.getEndDevice());
            sensorQ.setEndDevice(deviceQ);  // Assigner le endDeviceQ au capteur
        }
        return sensorQ;
    }

    public SensorQ mapperDto (SensorDTO sensor){
        SensorQ sen = new SensorQ();
        sen.setId(sensor.getId().toString());
        sen.setIdU(sensor.getIdU());
        sen.setIndex(sensor.getIndex());
        sen.setDescription(sensor.getDescription());
        sen.setLat(sensor.getLat());
        sen.setLongt(sensor.getLongt());
        sen.setAlertThersholdD(sensor.getAlertThersholdD());
        sen.setNormalThersholdD(sensor.getNormalThersholdD());
        sen.setAlertThersholdN(sensor.getAlertThersholdN());
        sen.setTypeSensor(sensor.getTypeSensor());
        sen.setNormalThersholdN(sensor.getNormalThersholdN());
       /* if(sensor.getDeviceId() != null){
            deviceRepo.findById(sensor.getDeviceId().toString())
                    .ifPresent(sen::setEndDevice);
        } else {
            sen.setEndDevice(null); // explicitement
        }*/


        return sen;
    }
}
