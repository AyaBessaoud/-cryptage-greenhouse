package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Query.entity.ActuatorHistoryQ;
import com.Agri.AgriBack.Query.services.ActHistoryQService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/actuatorHistory")
@RequiredArgsConstructor
public class ActHistoryQController {
    @Autowired
    private ActHistoryQService service;

    @GetMapping
    public ResponseEntity<List<ActuatorHistoryQ>> getAll(){
        return ResponseEntity.ok(service.getAllHistory());
    }
}
