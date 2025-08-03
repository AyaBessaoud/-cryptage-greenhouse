package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.CommandQ;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface CommandQRepo extends MongoRepository<CommandQ, String> {
}
