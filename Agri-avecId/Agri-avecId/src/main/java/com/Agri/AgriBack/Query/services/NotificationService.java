package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Query.entity.Notification;
import com.Agri.AgriBack.Query.repository.NotifRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotifRepo notificationRepo;

    public List<Notification> getNotificationsForUser(String userId) {
        return notificationRepo.findByUsers_Id(userId);
    }
}
