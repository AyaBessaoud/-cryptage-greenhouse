package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.PlanificationQ;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlanifQRepo extends MongoRepository<PlanificationQ,String> {
}
