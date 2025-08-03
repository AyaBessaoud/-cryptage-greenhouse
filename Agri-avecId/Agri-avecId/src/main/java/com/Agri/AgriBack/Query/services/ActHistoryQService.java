package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Query.entity.ActuatorHistoryQ;
import com.Agri.AgriBack.Query.entity.ActuatorQ;
import com.Agri.AgriBack.Query.repository.ActHistoryQRepo;
import com.Agri.AgriBack.Query.repository.ActuatorQRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActHistoryQService {
    @Autowired
    private ActHistoryQRepo repo;
    @Autowired
    private ActuatorQRepo actRepo;

    public List<ActuatorHistoryQ> getAllHistory(){
        List<ActuatorQ> actuators = actRepo.findAll();
        List<ActuatorHistoryQ> histories = new ArrayList<>();
        if(!actuators.isEmpty()){
            for(ActuatorQ act : actuators){
                ActuatorHistoryQ history = repo.findFirstByIdUOrderByDateDesc(act.getIdU());
                if (history != null) {
                    histories.add(history);
                }
            }
        }
         return histories;
    }
}
