package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Query.entity.EmployeeQ;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import com.Agri.AgriBack.Query.services.EndDeviceQService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/endDevice")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EndDeviceQController {

    @Autowired
    private EndDeviceQService service;

    @GetMapping("/user/{id}")
    public ResponseEntity<List<endDeviceQ>> getDevices(@PathVariable String id) {
        return ResponseEntity.ok(service.getAllDevicesByUser(id));
    }

    @GetMapping
    public ResponseEntity<List<endDeviceQ>> getAllDevices() {
        return ResponseEntity.ok(service.getAllDevices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<endDeviceQ> getDeviceById(@PathVariable String id){
        return ResponseEntity.ok(service.getDeviceById(id));
    }
}
