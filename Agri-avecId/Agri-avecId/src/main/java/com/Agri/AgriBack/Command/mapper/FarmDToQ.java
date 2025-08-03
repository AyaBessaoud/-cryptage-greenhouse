package com.Agri.AgriBack.Command.mapper;

import com.Agri.AgriBack.DTO.FarmDTO;
import com.Agri.AgriBack.Query.entity.FarmQ;
import com.Agri.AgriBack.Query.entity.GreenHouseQ;
import com.Agri.AgriBack.Query.repository.GreenHouseQRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class FarmDToQ {
    @Autowired
    private GreenHouseQRepo greenHouseRepo;

    public FarmQ farmMapper(FarmDTO dto){
        FarmQ farm = new FarmQ();
        farm.setId(dto.getId().toString());
        farm.setDescription(dto.getDescription());
        if(dto.getSerresIds() != null && !dto.getSerresIds().isEmpty()){
            List<GreenHouseQ> serres = dto.getSerresIds().stream().
                     map(id -> greenHouseRepo.findById(id.toString()).orElse(null))
                    .filter(Objects::nonNull) // filtre les serres non trouvés
                    .collect(Collectors.toList());
            farm.setSerres(serres);
        }else{
            farm.setSerres(null);
        }
        return  farm;
    }
}
