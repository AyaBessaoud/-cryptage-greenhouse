package com.Agri.AgriBack.Command.mapper;

import com.Agri.AgriBack.Command.entity.Command;
import com.Agri.AgriBack.Query.entity.CommandQ;
import org.springframework.stereotype.Component;

@Component
public class CommandCToQ {

    public CommandQ mapperCmd(Command command){
        CommandQ cmd = new CommandQ();
        cmd.setId(command.getId().toString());
        cmd.setName(command.getName());
        return cmd;
    }
}
