package com.Agri.AgriBack.Command.mapper;

import com.Agri.AgriBack.DTO.PlanificationDTO;
import com.Agri.AgriBack.Query.entity.ActuatorQ;
import com.Agri.AgriBack.Query.entity.PlanificationQ;
import com.Agri.AgriBack.Query.repository.ActuatorQRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PlanifDtoQ {
    @Autowired
    private ActuatorQRepo actRepo;

    public PlanificationQ mapperDTO(PlanificationDTO dto){
        PlanificationQ planif = new PlanificationQ();
        planif.setId(dto.getId().toString());
        planif.setEtat(dto.getEtat());
        planif.setDates(dto.getDates());
        planif.setDays(dto.getDays());
        planif.setMonths(dto.getMonths());
        planif.setTimes(dto.getTimes());
        planif.setPattern(dto.getPattern());
        planif.setBlocked(dto.isBlocked());
        planif.setDescription(dto.getDescription());
        planif.setFrame(dto.getFrame());
        planif.setCreationDate(dto.getCreationDate());
        if(dto.getActuatorId() != null){
            Optional<ActuatorQ> actOpt = actRepo.findById(dto.getActuatorId().toString());
            if(actOpt.isPresent()){
                ActuatorQ act = actOpt.get();
                planif.setActuator(act);
            }else{
                planif.setActuator(null);
            }
        }
        return  planif;
    }
}
