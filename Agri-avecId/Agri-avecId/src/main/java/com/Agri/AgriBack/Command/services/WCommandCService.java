package com.Agri.AgriBack.Command.services;

import com.Agri.AgriBack.Command.entity.CmdWEtat;
import com.Agri.AgriBack.Command.entity.WaitCommand;
import com.Agri.AgriBack.Command.producer.WaitCmdProducer;
import com.Agri.AgriBack.Command.repository.WCommandCRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WCommandCService {
    @Autowired
    private WCommandCRepo repo;
    @Autowired
    private WaitCmdProducer producer;

    public WaitCommand createWCmd(CmdWEtat cmd){
        WaitCommand command = new WaitCommand();
        command.setCommand(cmd.getCommand());
        command.setCodDevice(cmd.getCodDevice());
        command.setIdU(cmd.getIdU());
        WaitCommand savedCommand = repo.save(command);
        if (!"DOA".equals(cmd.getCommand())){
            producer.createCommandEvent(savedCommand);
        } else{
            producer.createActionEvent(cmd);
        }
        return savedCommand;
    }
}
