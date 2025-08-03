package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Query.entity.SensorQ;
import com.Agri.AgriBack.Query.entity.endDeviceQ;
import com.Agri.AgriBack.Query.services.SensorQService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sensor")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SensorQController {

    @Autowired
    private SensorQService service;

    @GetMapping
    public ResponseEntity<List<SensorQ>> getsensorss() {
        return ResponseEntity.ok(service.getAllSensors());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<SensorQ>> getsensorsByUserId(@PathVariable String id) {
        return ResponseEntity.ok(service.getSensorsByUser(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorQ> getSnesorById(@PathVariable String id){
        return ResponseEntity.ok(service.getSensorById(id));
    }

}
