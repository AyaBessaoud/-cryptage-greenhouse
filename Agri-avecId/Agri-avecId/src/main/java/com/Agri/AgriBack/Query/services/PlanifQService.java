package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Query.entity.PlanificationQ;
import com.Agri.AgriBack.Query.repository.PlanifQRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
    public class PlanifQService {
        @Autowired
        private PlanifQRepo repo;

        public List<PlanificationQ> getAll(){
            List<PlanificationQ> planifs = repo.findAll();
            return planifs;
        }

        public PlanificationQ getPlanifById(String id){
            Optional<PlanificationQ> planif =  repo.findById(id);
            return planif.orElse(null);
        }
    }

