package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.SensorType;
import com.Agri.AgriBack.DTO.ActuatorHistoryDTO;
import com.Agri.AgriBack.DTO.SensorHistoryDTO;
import com.Agri.AgriBack.Query.entity.*;
import com.Agri.AgriBack.Query.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LogsService {
    @Autowired
    private DeviceHistoryRepo deviceRepo;
    @Autowired
    private ActHistoryQRepo actuatorRepo;
    @Autowired
    private SensorHistoryRepo sensorRepo;
    @Autowired
    private GreenHouseQRepo serreRepo;
    @Autowired
    private ActuatorQRepo actuatorQRepo;
    @Autowired
    private SensorQRepo sensorQRepo;

    public Map<String, Object> getLatestDeviceData(String codDevice) {
        Map<String, Object> result = new HashMap<>();

        // Dernier deviceHistory
        result.put("deviceHistory", deviceRepo.findTopByCodDeviceOrderByDateDesc(codDevice).orElse(null));

        // Derniers actuators
        Map<String, ActuatorHistoryQ> actuators = new HashMap<>();
        for (Actuator.Outputs output : Actuator.Outputs.values()) {
            actuatorRepo.findTopByCodDeviceAndOutputOrderByDateDesc(codDevice, output)
                    .ifPresent(a -> actuators.put(output.name(), a));
        }
        result.put("actuators", actuators);

        // Derniers capteurs
        Map<String, SensorHistory> sensors = new HashMap<>();
        for (SensorType type : SensorType.values()) {
            sensorRepo.findTopByCodDeviceAndTypeSensorOrderByDateDesc(codDevice, type)
                    .ifPresent(s -> sensors.put(type.name(), s));
        }
        result.put("sensors", sensors);

        return result;
    }

    public Map<String, Object> getLatestDataBySerre(String serreId) {
        Map<String, Object> result = new HashMap<>();

        Optional<GreenHouseQ> optionalSerre = serreRepo.findById(serreId);
        if (optionalSerre.isEmpty()) return result;

        GreenHouseQ serre = optionalSerre.get();
        List<endDeviceQ> devices = serre.getDevices();
        if (devices == null || devices.isEmpty()) return result;

        Map<String, Map<String, Object>> deviceDataMap = new HashMap<>();

        for (endDeviceQ device : devices) {
            String codDevice = device.getCodDevice();
            Map<String, Object> deviceData = new HashMap<>();

            // Device History
            Optional<DeviceHistory> historyOpt = deviceRepo.findTopByCodDeviceOrderByDateDesc(codDevice);
            List<DeviceHistory> historyList = historyOpt.map(List::of).orElse(Collections.emptyList());
            deviceData.put("deviceHistory", historyList);

            // Actuators
            List<ActuatorHistoryDTO> actuators = new ArrayList<>();
            for (Actuator.Outputs output : Actuator.Outputs.values()) {
                actuatorRepo.findTopByCodDeviceAndOutputOrderByDateDesc(codDevice, output)
                        .ifPresent(history -> {
                            String description = actuatorQRepo.findByIdU(history.getIdU())
                                    .map(ActuatorQ::getDescription)
                                    .orElse("");

                            ActuatorHistoryDTO dto = new ActuatorHistoryDTO();
                            dto.setOutput(output.name());
                            dto.setDescription(description);
                            dto.setDate(history.getDate());
                            dto.setEtat(history.getEtat());
                            dto.setIndex(history.getIndex());
                            dto.setCodDevice(history.getCodDevice());
                            dto.setIdU(history.getIdU());

                            actuators.add(dto);
                        });
            }
            deviceData.put("actuators", actuators);

            // Sensors
            List<SensorHistoryDTO> sensors = new ArrayList<>();
            for (SensorType type : SensorType.values()) {
                sensorRepo.findTopByCodDeviceAndTypeSensorOrderByDateDesc(codDevice, type)
                        .ifPresent(history -> {
                            String description = "";
                            SensorQ sensorQ = sensorQRepo.findByIdU(history.getIdU());
                            if (sensorQ != null) {
                                description = sensorQ.getDescription();
                            }

                            SensorHistoryDTO dto = new SensorHistoryDTO();
                            dto.setTypeSensor(history.getTypeSensor());
                            dto.setDescription(description);
                            dto.setDate(history.getDate());
                            dto.setValue(history.getValue());
                            dto.setEtat(history.getEtat());
                            dto.setCodDevice(history.getCodDevice());
                            dto.setIdU(history.getIdU());
                            dto.setIndex(history.getIndex());

                            sensors.add(dto);
                        });
            }
            deviceData.put("sensors", sensors);

            deviceDataMap.put(codDevice, deviceData);
        }

        return Map.copyOf(deviceDataMap);
    }



}
