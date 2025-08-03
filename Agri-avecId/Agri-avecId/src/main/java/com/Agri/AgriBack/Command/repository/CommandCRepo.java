package com.Agri.AgriBack.Command.repository;

import com.Agri.AgriBack.Command.entity.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandCRepo extends JpaRepository<Command, Long> {
}
