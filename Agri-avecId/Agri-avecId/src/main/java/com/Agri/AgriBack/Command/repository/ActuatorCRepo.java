package com.Agri.AgriBack.Command.repository;

import com.Agri.AgriBack.Command.entity.Actuator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActuatorCRepo extends JpaRepository<Actuator, Long> {
}
