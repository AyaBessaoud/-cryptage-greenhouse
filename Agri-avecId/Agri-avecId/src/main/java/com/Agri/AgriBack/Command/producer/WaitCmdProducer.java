package com.Agri.AgriBack.Command.producer;

import com.Agri.AgriBack.Command.entity.CmdWEtat;
import com.Agri.AgriBack.Command.entity.Command;
import com.Agri.AgriBack.Command.entity.WaitCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitCmdProducer {

    private final KafkaTemplate<String, WaitCommand> kafkaTemplate;
    private final KafkaTemplate<String, CmdWEtat> kafkaTemplate2;




    public void createCommandEvent(WaitCommand command) {
        kafkaTemplate.send("WaitCommand_created", command);
    }

    public void createActionEvent(CmdWEtat command) {
        kafkaTemplate2.send("GetData_created", command);
    }
}
