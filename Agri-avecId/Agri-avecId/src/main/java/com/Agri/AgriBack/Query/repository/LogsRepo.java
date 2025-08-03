package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.Logs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogsRepo extends MongoRepository<Logs,String> {
    Page<Logs> findByCodDevice(String code, Pageable pageable);
}
