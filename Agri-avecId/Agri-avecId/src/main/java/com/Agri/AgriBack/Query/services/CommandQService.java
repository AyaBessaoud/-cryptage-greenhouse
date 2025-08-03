package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Query.entity.CommandQ;
import com.Agri.AgriBack.Query.repository.CommandQRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandQService {
    @Autowired
    private CommandQRepo repo;

    public List<CommandQ> findAllCmds(){
      return repo.findAll();
    }
}
