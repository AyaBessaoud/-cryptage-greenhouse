package com.Agri.AgriBack.Command.repository;

import com.Agri.AgriBack.Command.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorCRepo extends JpaRepository<Sensor, Long> {
}
