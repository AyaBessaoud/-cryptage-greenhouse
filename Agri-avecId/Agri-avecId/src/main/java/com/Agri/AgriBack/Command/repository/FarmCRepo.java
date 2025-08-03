package com.Agri.AgriBack.Command.repository;

import com.Agri.AgriBack.Command.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmCRepo extends JpaRepository<Farm,Long> {
}
