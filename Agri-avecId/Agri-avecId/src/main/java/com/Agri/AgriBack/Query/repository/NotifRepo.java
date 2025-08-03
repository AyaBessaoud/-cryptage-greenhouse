package com.Agri.AgriBack.Query.repository;

import com.Agri.AgriBack.Query.entity.Notification;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotifRepo extends MongoRepository<Notification, String> {
    // Trouve toutes les notifications où l'utilisateur est dans la liste users
    List<Notification> findByUsers_Id(String userId);
}
