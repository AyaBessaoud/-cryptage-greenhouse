package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.Query.entity.EmployeeQ;
import com.Agri.AgriBack.Query.entity.GreenHouseQ;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmployeeQRepo extends MongoRepository<EmployeeQ, String> {
    List<EmployeeQ> findBySerre(GreenHouseQ serre);
    List<EmployeeQ> findByRole(Employee.Role role);
}
