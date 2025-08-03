package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Command.entity.SensorType;
import com.Agri.AgriBack.Query.entity.SensorHistory;
import com.Agri.AgriBack.Query.repository.SensorHistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SensorHistoryService {
    @Autowired
    private SensorHistoryRepo repo;

    public Map<SensorType, List<SensorHistory>> getLast24HoursData(String codDevice) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);

        // Récupération des données filtrées
        List<SensorHistory> records = repo.findByCodDeviceAndDateAfter(codDevice, twentyFourHoursAgo);

        // Gestion du cas où il n'y a pas de données
        if (records == null || records.isEmpty()) {
            return Collections.emptyMap(); // ou renvoyer un message ou logger un avertissement
        }

        // Organisation par type de capteur
        return records.stream()
                .collect(Collectors.groupingBy(SensorHistory::getTypeSensor));
    }
}
