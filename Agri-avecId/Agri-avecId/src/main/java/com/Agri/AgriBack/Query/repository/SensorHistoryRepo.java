package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.SensorHistory;
import com.Agri.AgriBack.Command.entity.SensorType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorHistoryRepo extends MongoRepository<SensorHistory,String> {
    Optional<SensorHistory> findTopByCodDeviceAndTypeSensorOrderByDateDesc(String codDevice, SensorType typeSensor);
    List<SensorHistory> findByCodDeviceAndDateAfter(String codDevice, LocalDateTime date);
}
