package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Query.entity.Notification;
import com.Agri.AgriBack.Query.repository.NotifRepo;
import com.Agri.AgriBack.Query.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private NotificationService service;
    @Autowired
    private NotifRepo repo;

    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable String userId) {
        return service.getNotificationsForUser(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotif (@PathVariable String id){
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
