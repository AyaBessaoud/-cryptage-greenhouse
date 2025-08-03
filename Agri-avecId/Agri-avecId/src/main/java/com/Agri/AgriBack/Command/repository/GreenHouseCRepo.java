package com.Agri.AgriBack.Command.repository;

import com.Agri.AgriBack.Command.entity.GreenHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GreenHouseCRepo extends JpaRepository<GreenHouse,Long> {
}
