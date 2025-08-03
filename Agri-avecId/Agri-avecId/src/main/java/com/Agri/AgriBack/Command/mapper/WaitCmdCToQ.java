package com.Agri.AgriBack.Command.mapper;

import com.Agri.AgriBack.Command.entity.CmdWEtat;
import com.Agri.AgriBack.Command.entity.WaitCommand;
import com.Agri.AgriBack.Query.entity.WaitCommantQ;
import org.springframework.stereotype.Component;

@Component
public class WaitCmdCToQ {

    public WaitCommantQ mapper(WaitCommand command){
        WaitCommantQ cmd = new WaitCommantQ();
        cmd.setId(command.getId().toString());
        cmd.setCommand(command.getCommand());
        cmd.setCodDevice(command.getCodDevice());
        cmd.setIdU(command.getIdU());
        return cmd;
    }
}
