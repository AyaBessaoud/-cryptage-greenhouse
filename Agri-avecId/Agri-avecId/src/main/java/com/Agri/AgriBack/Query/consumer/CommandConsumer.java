package com.Agri.AgriBack.Query.consumer;

import com.Agri.AgriBack.Command.entity.Command;
import com.Agri.AgriBack.Command.mapper.CommandCToQ;
import com.Agri.AgriBack.Query.entity.CommandQ;
import com.Agri.AgriBack.Query.repository.CommandQRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandConsumer {
    @Autowired
    private CommandQRepo repo;
    @Autowired
    private CommandCToQ mapper;

    @KafkaListener(topics = "Command_created", groupId = "Agri-group")
    public CommandQ CreateEvent(Command command){
        CommandQ cmd = mapper.mapperCmd(command);
        return repo.save(cmd);
    }
}
