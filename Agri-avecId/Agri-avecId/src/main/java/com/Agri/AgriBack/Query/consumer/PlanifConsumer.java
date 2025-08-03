package com.Agri.AgriBack.Query.consumer;

import com.Agri.AgriBack.Command.mapper.PlanifDtoQ;
import com.Agri.AgriBack.DTO.PlanificationDTO;
import com.Agri.AgriBack.Query.entity.PlanificationQ;
import com.Agri.AgriBack.Query.repository.PlanifQRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanifConsumer {
    @Autowired
    private PlanifQRepo repo;
    @Autowired
    private PlanifDtoQ mapper;

    @KafkaListener(topics = "Planif_created", groupId = "Agri-group")
    public void createPlanif(PlanificationDTO dto){
        PlanificationQ planif = mapper.mapperDTO(dto);
        repo.save(planif);
    }

    @KafkaListener(topics = "Planif_updated", groupId = "Agri-group")
    public void updatePlanif(PlanificationDTO dto){
        PlanificationQ planif = mapper.mapperDTO(dto);
        repo.save(planif);
    }
}
