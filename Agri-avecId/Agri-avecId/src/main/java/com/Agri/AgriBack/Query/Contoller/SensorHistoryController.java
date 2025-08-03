package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Command.entity.SensorType;
import com.Agri.AgriBack.Query.entity.SensorHistory;
import com.Agri.AgriBack.Query.services.SensorHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sensor/history")
public class SensorHistoryController {
    @Autowired
    private SensorHistoryService service;

    @GetMapping
    public ResponseEntity<Map<SensorType, List<SensorHistory>>> getLast24HoursHistory(
            @RequestParam String codDevice) {
        Map<SensorType, List<SensorHistory>> result = service.getLast24HoursData(codDevice);

        if (result.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(result); // 200 OK
    }
}
