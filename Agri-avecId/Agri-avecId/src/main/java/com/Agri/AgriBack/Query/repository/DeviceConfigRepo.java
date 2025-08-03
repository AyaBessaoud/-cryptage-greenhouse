package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.DeviceConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DeviceConfigRepo extends MongoRepository<DeviceConfig, String> {
    Optional<DeviceConfig> findByCodDevice(String codDevice);

}
