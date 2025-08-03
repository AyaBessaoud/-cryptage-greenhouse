package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.ActuatorHistoryQ;
import com.Agri.AgriBack.Command.entity.Actuator;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ActHistoryQRepo extends MongoRepository<ActuatorHistoryQ, String> {
    Optional<ActuatorHistoryQ> findTopByCodDeviceAndOutputOrderByDateDesc(String codDevice, Actuator.Outputs output);
    // Récupère le dernier historique (plus récent) trié par date décroissante et idU
    ActuatorHistoryQ findFirstByIdUOrderByDateDesc(String idU);
}
