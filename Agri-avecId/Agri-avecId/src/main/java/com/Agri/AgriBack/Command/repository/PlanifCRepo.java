package com.Agri.AgriBack.Command.repository;

import com.Agri.AgriBack.Command.entity.Planification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanifCRepo extends JpaRepository<Planification,Long> {
}
