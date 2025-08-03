package com.Agri.AgriBack.Command.services;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.Planification;
import com.Agri.AgriBack.Command.producer.PlanifProducer;
import com.Agri.AgriBack.Command.repository.ActuatorCRepo;
import com.Agri.AgriBack.Command.repository.PlanifCRepo;
import com.Agri.AgriBack.DTO.CreatePlanif;
import com.Agri.AgriBack.DTO.PlanificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PlanifCService {
    @Autowired
    private PlanifCRepo repo;
    @Autowired
    private PlanifProducer producer;
    @Autowired
    private ActuatorCRepo actRepo;

    public Planification CreatePlanif(CreatePlanif createPlanif){
        Planification planification = new Planification();
        planification.setDates(createPlanif.getDates());
        planification.setDays(createPlanif.getDays());
        planification.setTimes(createPlanif.getTimes());
        planification.setMonths(createPlanif.getMonths());
        planification.setPattern(createPlanif.getPattern());
        planification.setEtat(createPlanif.getEtat());
        planification.setDescription(createPlanif.getDescription());
        planification.setBlocked(false);
        planification.setCreationDate(LocalDateTime.now());
        if(createPlanif.getActuator() != null){
            Optional<Actuator> actOpt = actRepo.findById(createPlanif.getActuator().getId());
            if(actOpt.isPresent()){
                Actuator act = actOpt.get();
                String frame = DoACommand(act.getDevice().getCodDevice(),act.getOutput(), createPlanif.getEtat(),act.getIndex());
                planification.setFrame(frame);
                planification.setActuator(act);
            }
            else{
                planification.setFrame(null);
                planification.setActuator(null);
            }
        }
        Planification savedPlanif = repo.save(planification);
        PlanificationDTO dto = mapper(savedPlanif);
        producer.createPlanifEvent(dto);
        return savedPlanif;
    }

    public Planification updatePlanif(Planification planif){
        if(planif.getActuator() != null){
            Optional<Actuator> actOpt = actRepo.findById(planif.getActuator().getId());
            if(actOpt.isPresent()){
                Actuator act = actOpt.get();
                planif.setActuator(act);
                String frame = DoACommand(act.getDevice().getCodDevice(),act.getOutput(), planif.getEtat(),act.getIndex());
                planif.setFrame(frame);
            }else{
                planif.setActuator(null);
                planif.setFrame(null);
            }
        }

        Planification updatedPlanif = repo.save(planif);
        PlanificationDTO dto = mapper(updatedPlanif);
        producer.updatePlanifEvent(dto);
        return updatedPlanif;
    }

    public String DoACommand(String codDevice, Actuator.Outputs output,int etat ,int index) {
        return String.format("3C;%s;%s;%d;%d;", codDevice,output,etat, index);
    }

    private PlanificationDTO mapper(Planification planif){
        PlanificationDTO dto = new PlanificationDTO();
        dto.setPattern(planif.getPattern());
        dto.setDates(planif.getDates());
        dto.setEtat(planif.getEtat());
        dto.setDescription(planif.getDescription());
        dto.setBlocked(planif.isBlocked());
        dto.setId(planif.getId());
        dto.setActuatorId(planif.getActuator().getId());
        dto.setFrame(planif.getFrame());
        dto.setCreationDate(planif.getCreationDate());
        dto.setDays(planif.getDays());
        dto.setMonths(planif.getMonths());
        dto.setTimes(planif.getTimes());
        return dto;
    }
}
