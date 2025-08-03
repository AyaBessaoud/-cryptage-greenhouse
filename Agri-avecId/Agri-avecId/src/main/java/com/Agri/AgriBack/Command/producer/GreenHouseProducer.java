package com.Agri.AgriBack.Command.producer;

import com.Agri.AgriBack.DTO.GreenHouseDTO;
import com.Agri.AgriBack.DTO.SensorDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class GreenHouseProducer {
    private final KafkaTemplate<String, GreenHouseDTO> kafkaTemplate;
    private final KafkaTemplate<String, Long> kafkaTemplateLong; // Template pour les IDs

    public GreenHouseProducer(KafkaTemplate<String, GreenHouseDTO> kafkaTemplate, KafkaTemplate<String, Long> kafkaTemplateLong) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplateLong = kafkaTemplateLong;
    }

    public void createGreenHouseEvent(GreenHouseDTO dto) {
        kafkaTemplate.send("GreenHouse_created", dto);
    }

    public void updateGreenHouseEvent(GreenHouseDTO dto) {
        kafkaTemplate.send("GreenHouse_updated", dto);
    }
}
