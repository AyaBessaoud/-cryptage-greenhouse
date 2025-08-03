package com.Agri.AgriBack.Command.mapper;

import com.Agri.AgriBack.Command.repository.FarmCRepo;
import com.Agri.AgriBack.DTO.GreenHouseDTO;
import com.Agri.AgriBack.Query.entity.FarmQ;
import com.Agri.AgriBack.Query.entity.GreenHouseQ;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import com.Agri.AgriBack.Query.repository.EndDeviceQRepo;
import com.Agri.AgriBack.Query.repository.FarmQRepo;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
public class GreenHouseDtoQ {
    @Autowired
    private EndDeviceQRepo deviceRepo;
    @Autowired
    private FarmQRepo farmRepo;
    public GreenHouseQ mapperDto(GreenHouseDTO dto){
        GreenHouseQ serre = new GreenHouseQ();
        serre.setId(dto.getId().toString());
        serre.setDescription(dto.getDescription());
        if(dto.getFermeId() != null){
          Optional<FarmQ> fermeOpt =   farmRepo.findById(dto.getFermeId().toString());
            if(fermeOpt.isPresent()){
                FarmQ ferme  =  fermeOpt.get();
                serre.setFerme(ferme);
            }
        }
        else{
            serre.setFerme(null);
        }
        if(dto.getDeviceIds() != null && !dto.getDeviceIds().isEmpty()){
            List<endDeviceQ> devices = dto.getDeviceIds().stream()
                    .map(id -> deviceRepo.findById(id.toString()).orElse(null))
                     .filter(Objects::nonNull) // filtre les actionneurs non trouvés
                    .collect(Collectors.toList());
            serre.setDevices(devices);
        }
        else{
            serre.setDevices(new ArrayList<>());
        }

        return serre;
    }
}
