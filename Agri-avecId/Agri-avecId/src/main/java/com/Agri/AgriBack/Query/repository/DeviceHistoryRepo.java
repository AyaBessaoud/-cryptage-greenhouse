package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.DeviceHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DeviceHistoryRepo extends MongoRepository<DeviceHistory,String> {
    Optional<DeviceHistory> findTopByCodDeviceOrderByDateDesc(String codDevice);
}
