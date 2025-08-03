package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.FarmQ;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FarmQRepo extends MongoRepository<FarmQ,String> {
}
