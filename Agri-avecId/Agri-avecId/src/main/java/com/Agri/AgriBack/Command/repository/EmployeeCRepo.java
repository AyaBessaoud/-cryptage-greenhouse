package com.Agri.AgriBack.Command.repository;

import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.Command.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeCRepo extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByEmailAndPassword(String email, String password);
}
