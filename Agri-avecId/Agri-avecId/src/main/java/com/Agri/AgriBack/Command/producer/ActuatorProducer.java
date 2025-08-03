package com.Agri.AgriBack.Command.producer;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.DTO.ActuatorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActuatorProducer {

    private final KafkaTemplate<String, ActuatorDTO> kafkaTemplate;
    private final KafkaTemplate<String, Long> kafkaTemplateLong;



    public void createActuatorEvent(ActuatorDTO actuator) {
        kafkaTemplate.send("Actuator_created", actuator);
    }

    public void updateActuatorEvent(ActuatorDTO actuator) {
        kafkaTemplate.send("Actuator_updated", actuator);
    }

    public void deleteActuatorEvent(Long actuatorId) {
        kafkaTemplateLong.send("Actuator_deleted", actuatorId);
    }
}
