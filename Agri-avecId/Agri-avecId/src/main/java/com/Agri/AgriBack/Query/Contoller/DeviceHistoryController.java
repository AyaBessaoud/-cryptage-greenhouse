package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Query.entity.DeviceHistory;
import com.Agri.AgriBack.Query.services.DeviceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/deviceHistory")
@RequiredArgsConstructor
public class DeviceHistoryController {
    @Autowired
    private DeviceHistoryService service;

    @GetMapping
    public ResponseEntity<List<DeviceHistory>> getLatesHistories(){
        return ResponseEntity.ok(service.getHistories());
    }
}
