package com.Agri.AgriBack.Command.producer;

import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.DTO.EmployeeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeProducer {
    private final KafkaTemplate<String, EmployeeDTO> kafkaTemplate;
    private final KafkaTemplate<String, Long> kafkaTemplateLong;

    public void createEmployeeEvent(EmployeeDTO employee) {
        kafkaTemplate.send("Employee_created", employee);
    }

    public void updateEmployeeEvent(EmployeeDTO employee) {
        kafkaTemplate.send("Employee_updated", employee);
    }

    public void deleteEmployeeEvent(Long id) {
        kafkaTemplateLong.send("Employee_deleted", id);
    }


}
