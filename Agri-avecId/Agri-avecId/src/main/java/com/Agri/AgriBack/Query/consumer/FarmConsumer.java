package com.Agri.AgriBack.Query.consumer;

import com.Agri.AgriBack.Command.entity.Farm;

import com.Agri.AgriBack.Command.mapper.FarmDToQ;
import com.Agri.AgriBack.DTO.FarmDTO;
import com.Agri.AgriBack.Query.entity.FarmQ;
import com.Agri.AgriBack.Query.repository.FarmQRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FarmConsumer {
    @Autowired
    private FarmQRepo repo;
    @Autowired
    private FarmDToQ mapprer;

    @KafkaListener(topics = "Farm_created", groupId = "Agri-group")
    public void consumeCreateEvent(FarmDTO dto){
        FarmQ farm = mapprer.farmMapper(dto);
        repo.save(farm);
    }

    @KafkaListener(topics = "Farm_updated", groupId = "Agri-group")
    public void consumeUpdatedEvent(FarmDTO dto){
        FarmQ farm = mapprer.farmMapper(dto);
        repo.save(farm);
    }
}
