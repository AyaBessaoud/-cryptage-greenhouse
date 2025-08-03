package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.GetDataConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GetDConfigRepo extends MongoRepository<GetDataConfig, String> {
}
