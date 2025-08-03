package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.GreenHouseQ;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GreenHouseQRepo extends MongoRepository<GreenHouseQ,String> {
}
