package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.DoAConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DoAConfigRepo extends MongoRepository<DoAConfig, String> {
}
