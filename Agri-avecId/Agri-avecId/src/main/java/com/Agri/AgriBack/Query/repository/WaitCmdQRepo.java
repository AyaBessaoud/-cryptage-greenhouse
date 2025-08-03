package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.WaitCommantQ;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WaitCmdQRepo extends MongoRepository<WaitCommantQ, String> {
}
