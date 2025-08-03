package com.Agri.AgriBack.Command.producer;

import com.Agri.AgriBack.DTO.FarmDTO;
import com.Agri.AgriBack.DTO.PlanificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanifProducer {
    private final KafkaTemplate<String, PlanificationDTO> kafkaTemplate;

    public void createPlanifEvent(PlanificationDTO planif) {
        kafkaTemplate.send("Planif_created", planif);
    }

    public void updatePlanifEvent(PlanificationDTO planif) {
        kafkaTemplate.send("Planif_updated", planif);
    }
}
