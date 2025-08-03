package com.Agri.AgriBack.Command.producer;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandProducer {

    private final KafkaTemplate<String, Command> kafkaTemplate;


    public void createCommandEvent(Command command) {
        kafkaTemplate.send("Command_created", command);
    }
}
