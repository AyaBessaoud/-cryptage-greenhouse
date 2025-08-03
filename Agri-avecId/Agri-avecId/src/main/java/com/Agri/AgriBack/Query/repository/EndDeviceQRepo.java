package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.endDeviceQ;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndDeviceQRepo extends MongoRepository<endDeviceQ, String> {
    @Aggregation(pipeline = {
            "{ $lookup: { from: 'Sensor', localField: 'sensors', foreignField: '_id', as: 'sensors' } }"
    })
    List<endDeviceQ> findAllWithSensors();

    endDeviceQ findByCodDevice(String codDevice);
}
