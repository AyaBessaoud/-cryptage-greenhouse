package com.Agri.AgriBack.Command.services;

import com.Agri.AgriBack.Command.entity.Command;
import com.Agri.AgriBack.Command.producer.CommandProducer;
import com.Agri.AgriBack.Command.repository.CommandCRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CmdCService {
    @Autowired
    private CommandCRepo repo;
    @Autowired
    private CommandProducer producer;

    public Command createCommand(Command command){
        Command cmd = repo.save(command);
        producer.createCommandEvent(cmd);
        return cmd;
    }
}
