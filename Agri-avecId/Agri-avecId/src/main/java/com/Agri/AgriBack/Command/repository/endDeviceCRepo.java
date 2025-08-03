package com.Agri.AgriBack.Command.repository;

import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.Command.entity.endDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface endDeviceCRepo extends JpaRepository<endDevice, Long> {
}
