package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.ActuatorConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ActuatorConfigRepo extends MongoRepository<ActuatorConfig, String> {
    Optional<ActuatorConfig> findByIdU(String idU);
}
