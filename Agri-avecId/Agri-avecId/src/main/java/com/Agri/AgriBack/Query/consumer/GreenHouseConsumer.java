package com.Agri.AgriBack.Query.consumer;

import com.Agri.AgriBack.Command.entity.Farm;
import com.Agri.AgriBack.Command.mapper.GreenHouseDtoQ;
import com.Agri.AgriBack.DTO.GreenHouseDTO;
import com.Agri.AgriBack.Query.entity.FarmQ;
import com.Agri.AgriBack.Query.entity.GreenHouseQ;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import com.Agri.AgriBack.Query.repository.EndDeviceQRepo;
import com.Agri.AgriBack.Query.repository.FarmQRepo;
import com.Agri.AgriBack.Query.repository.GreenHouseQRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GreenHouseConsumer {
    @Autowired
    private GreenHouseDtoQ dtoq;
    @Autowired
    private EndDeviceQRepo deviceRepo;
    @Autowired
    private FarmQRepo farmRepo;
    @Autowired
    private GreenHouseQRepo repo;

    @KafkaListener(topics = "GreenHouse_created", groupId = "Agri-group")
    public void consumeCreateEvent(GreenHouseDTO dto){
        GreenHouseQ serre= dtoq.mapperDto(dto);

        //on ajoute la serre dans la liste des serres de la ferme
        if(dto.getFermeId() != null) {
            Optional<FarmQ> fermeOpt = farmRepo.findById(dto.getFermeId().toString());
            if (fermeOpt.isPresent()) {
                FarmQ ferme  =  fermeOpt.get();
                List<GreenHouseQ> serres = ferme.getSerres();
                if(serres ==null){
                    serres = new ArrayList<>();
                }
                serres.add(serre);
                ferme.setSerres(serres);
                farmRepo.save(ferme);
            }
        }
        repo.save(serre);
    }

    @KafkaListener(topics = "GreenHouse_updated", groupId = "Agri-group")
    public void consumeUpdateEvent(GreenHouseDTO dto){
        //on supprime la serre du list serre de la ferme
        List<FarmQ> farms = farmRepo.findAll();
        for(FarmQ farm : farms){
            List<GreenHouseQ> serres = farm.getSerres();
            if(serres != null && !serres.isEmpty()){
                serres.removeIf(s -> s.getId().equals(dto.getId().toString()));
                farm.setSerres(serres);
                farmRepo.save(farm);
            }
        }

        GreenHouseQ serre = dtoq.mapperDto(dto);
        repo.save(serre);

        Optional<FarmQ> farmOpt = farmRepo.findById(dto.getFermeId().toString());
        if(farmOpt.isPresent()){
            FarmQ ferme  =  farmOpt.get();
            List<GreenHouseQ> serres = ferme.getSerres();
            if(serres ==null || serres.isEmpty()){
                serres = new ArrayList<>();
            }
            serres.add(serre);
            ferme.setSerres(serres);
            farmRepo.save(ferme);
        }
    }
}
