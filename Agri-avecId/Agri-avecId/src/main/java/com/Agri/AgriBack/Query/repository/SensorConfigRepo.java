package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.SensorConfig;
import org.springframework.data.mongodb.core.MongoAdminOperations;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SensorConfigRepo extends MongoRepository<SensorConfig, String> {
    Optional<SensorConfig> findByIdU(String idU);
}
