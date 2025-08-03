package com.Agri.AgriBack.Command.repository;

import com.Agri.AgriBack.Command.entity.WaitCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WCommandCRepo extends JpaRepository<WaitCommand, Long> {
}
