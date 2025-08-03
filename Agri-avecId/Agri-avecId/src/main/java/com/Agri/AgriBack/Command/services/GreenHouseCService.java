package com.Agri.AgriBack.Command.services;

import com.Agri.AgriBack.Command.entity.Farm;
import com.Agri.AgriBack.Command.entity.GreenHouse;
import com.Agri.AgriBack.Command.entity.endDevice;
import com.Agri.AgriBack.Command.producer.GreenHouseProducer;
import com.Agri.AgriBack.Command.repository.FarmCRepo;
import com.Agri.AgriBack.Command.repository.GreenHouseCRepo;
import com.Agri.AgriBack.Command.repository.endDeviceCRepo;
import com.Agri.AgriBack.DTO.GreenHouseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GreenHouseCService {
    @Autowired
    private GreenHouseCRepo repo;
    @Autowired
    private GreenHouseProducer producer;
    @Autowired
    private FarmCRepo farmRepo;
    @Autowired
    private endDeviceCRepo deviceRepo;

    public GreenHouse createGreenHouse(GreenHouse serre){
        // Vérifie si la serre a une ferme
        if (serre.getFerme() != null && serre.getFerme().getId() != null) {
            Farm realFarm = farmRepo.findById(serre.getFerme().getId())
                    .orElseThrow(() -> new RuntimeException("Farm with ID " + serre.getFerme().getId() + " not found"));

            serre.setFerme(realFarm); // ⚠️ Associe la ferme persistée
        }
        GreenHouse savedGreenHouse = repo.save(serre);
        GreenHouseDTO dto = mapper(savedGreenHouse);
        producer.createGreenHouseEvent(dto);
        return savedGreenHouse;
    }

    public GreenHouse updateGreenHouse(GreenHouse serre){
        // Vérifie si la serre a une ferme
        if (serre.getFerme() != null && serre.getFerme().getId() != null) {
            Farm realFarm = farmRepo.findById(serre.getFerme().getId())
                    .orElseThrow(() -> new RuntimeException("Farm with ID " + serre.getFerme().getId() + " not found"));

            serre.setFerme(realFarm); // ⚠️ Associe la ferme persistée
        }

        // 🔁 2. Associer les appareils réels depuis la base
        if (serre.getDevices() != null && !serre.getDevices().isEmpty()) {
            List<endDevice> realDevices = new ArrayList<>();

            for (endDevice device : serre.getDevices()) {
                if (device.getId() != null) {
                    endDevice realDevice = deviceRepo.findById(device.getId())
                            .orElseThrow(() -> new RuntimeException("Device with ID " + device.getId() + " not found"));

                    realDevice.setSerre(serre); // ⚠️ Re-lie bien la serre à l'appareil (si relation bidirectionnelle)
                    realDevices.add(realDevice);
                }
            }

            serre.setDevices(realDevices); // ⬅️ Met à jour la liste avec des entités persistées
        }
        GreenHouse savedGreenHouse = repo.save(serre);
        GreenHouseDTO dto = mapper(savedGreenHouse);
        producer.updateGreenHouseEvent(dto);
        return savedGreenHouse;
    }

    public GreenHouseDTO mapper(GreenHouse serre){
        GreenHouseDTO dto = new GreenHouseDTO();
        dto.setId(serre.getId());
        dto.setDescription(serre.getDescription());
        if(serre.getFerme() != null){
            dto.setFermeId(serre.getFerme().getId());
        }else{
            dto.setFermeId(null);
        }
        List<Long> ids = new ArrayList<>();
        if(serre.getDevices() != null && !serre.getDevices().isEmpty()){
            ids = serre.getDevices()
                    .stream()
                    .map(endDevice::getId)
                    .collect(Collectors.toList());
        }
        dto.setDeviceIds(ids);
        return dto;
    }
}
