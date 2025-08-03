package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Query.entity.ActuatorQ;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ActuatorQRepo extends MongoRepository<ActuatorQ, String> {
    Optional<ActuatorQ> findByIdU(String idU);
}
