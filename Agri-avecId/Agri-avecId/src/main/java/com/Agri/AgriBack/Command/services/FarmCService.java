package com.Agri.AgriBack.Command.services;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.Farm;
import com.Agri.AgriBack.Command.entity.GreenHouse;
import com.Agri.AgriBack.Command.producer.FarmProducer;
import com.Agri.AgriBack.Command.repository.FarmCRepo;
import com.Agri.AgriBack.Command.repository.GreenHouseCRepo;
import com.Agri.AgriBack.DTO.FarmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FarmCService {
    @Autowired
    private FarmProducer producer;
    @Autowired
    private FarmCRepo repo;
    @Autowired
    private GreenHouseCRepo greenHouseRepo;

    public Farm createFarm(Farm farm){
        Farm savedFarm = repo.save(farm);
        FarmDTO dto = mapper(savedFarm);
        producer.createFarmEvent(dto);
        return savedFarm;
    }

    public Farm updateFarm(Farm farm){
        // Vérifie si la ferme a une liste de serres
        if (farm.getSerres() != null && !farm.getSerres().isEmpty()) {
            List<GreenHouse> realSerres = new ArrayList<>();

            for (GreenHouse serre : farm.getSerres()) {
                if (serre.getId() != null) {
                    GreenHouse realSerre = greenHouseRepo.findById(serre.getId())
                            .orElseThrow(() -> new RuntimeException("Serre with ID " + serre.getId() + " not found"));
                    realSerres.add(realSerre);
                }
            }

            farm.setSerres(realSerres); // ⚠️ On remplace la liste avec les vraies instances persistées
        }
        Farm savedFarm = repo.save(farm);
        FarmDTO dto = mapper(savedFarm);
        producer.updateFarmEvent(dto);
        return savedFarm;
    }

    public FarmDTO mapper(Farm farm){
        FarmDTO dto = new FarmDTO();
        dto.setDescription(farm.getDescription());
        dto.setId(farm.getId());
        List<Long> ids = new ArrayList<>();
        if (farm.getSerres() != null) {
            ids = farm.getSerres()
                    .stream()
                    .map(GreenHouse::getId)
                    .collect(Collectors.toList());
        }
        dto.setSerresIds(ids);

        return dto;
    }
}
