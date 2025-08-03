package com.Agri.AgriBack.Query.consumer;

import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.Command.mapper.EmployeeDToQ;
import com.Agri.AgriBack.DTO.EmployeeDTO;
import com.Agri.AgriBack.Query.entity.EmployeeQ;
import com.Agri.AgriBack.Query.repository.EmployeeQRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class employeeConsumer {

    @Autowired
    private EmployeeQRepo repo;
    @Autowired
    private EmployeeDToQ empDTO;

    @KafkaListener(topics = "Employee_created", groupId = "Agri-group")
    public void consumeCreateEvent(EmployeeDTO employee) {
        EmployeeQ emp = empDTO.mapperDto(employee);
        repo.save(emp);
    }

    @KafkaListener(topics = "Employee_updated", groupId = "Agri-group")
    public void consumeUpdateEvent(EmployeeDTO employee) {
        EmployeeQ emp = empDTO.mapperDto(employee);
        repo.save(emp);

    }

    @KafkaListener(topics = "Employee_deleted", groupId = "Agri-group")
    public void deleteEmployeeEvent(Long id){
        if(repo.existsById(id.toString())){
            repo.deleteById(id.toString());
        }
    }

}
