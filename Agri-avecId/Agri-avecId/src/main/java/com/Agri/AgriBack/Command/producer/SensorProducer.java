package com.Agri.AgriBack.Command.producer;

import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.DTO.SensorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SensorProducer {

    private final KafkaTemplate<String, SensorDTO> kafkaTemplate;
    private final KafkaTemplate<String, Long> kafkaTemplateLong; // Template pour les IDs


    public void createSensorEvent(SensorDTO sensor) {
        kafkaTemplate.send("Sensor_created", sensor);
    }

    public void updateSensorEvent(SensorDTO sensor) {
        kafkaTemplate.send("Sensor_updated", sensor);
    }

    public void deleteSensorEvent(Long sensorId) {
        System.out.println("🔹 Envoi de l'événement Kafka pour suppression du capteur ID: " + sensorId);
        kafkaTemplateLong.send("Sensor_deleted", sensorId);
    }
}
