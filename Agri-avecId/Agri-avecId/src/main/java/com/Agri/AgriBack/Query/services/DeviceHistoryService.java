package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Query.entity.DeviceHistory;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import com.Agri.AgriBack.Query.repository.DeviceConfigRepo;
import com.Agri.AgriBack.Query.repository.DeviceHistoryRepo;
import com.Agri.AgriBack.Query.repository.EndDeviceQRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceHistoryService {
    @Autowired
    private DeviceHistoryRepo repo;
    @Autowired
    private EndDeviceQRepo deviceRepo;

    public List<DeviceHistory> getHistories(){
        List<endDeviceQ> devices = deviceRepo.findAll();
        List<DeviceHistory> histories = new ArrayList<>();
        if(!devices.isEmpty()){
            for(endDeviceQ device : devices){
                Optional<DeviceHistory> optionalHistory = repo.findTopByCodDeviceOrderByDateDesc(device.getCodDevice());
                optionalHistory.ifPresent(histories::add); // ajoute s’il existe
            }
        }
        return histories;
    }
}
