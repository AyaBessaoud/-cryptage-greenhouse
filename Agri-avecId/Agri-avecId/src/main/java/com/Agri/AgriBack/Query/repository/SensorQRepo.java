package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Query.entity.SensorQ;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorQRepo extends MongoRepository<SensorQ, String> {
    SensorQ findByIdU(String idU);
}
