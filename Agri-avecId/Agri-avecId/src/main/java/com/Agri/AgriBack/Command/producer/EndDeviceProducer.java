package com.Agri.AgriBack.Command.producer;

import com.Agri.AgriBack.Command.entity.endDevice;
import com.Agri.AgriBack.DTO.EndDeviceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EndDeviceProducer {
    private final KafkaTemplate<String, EndDeviceDTO> kafkaTemplate;
    private final KafkaTemplate<String, Long> kafkaTemplateLong; // Template pour les IDs



    public void createEndDeviceEvent(EndDeviceDTO device) {
        kafkaTemplate.send("EndDevice_created", device);
    }

    public void updateEndDeviceEvent(EndDeviceDTO device) {
        kafkaTemplate.send("EndDevice_updated", device);
    }

    public void deleteEndDeviceEvent(Long deviceId) {
        kafkaTemplateLong.send("EndDevice_deleted", deviceId);
    }
}
