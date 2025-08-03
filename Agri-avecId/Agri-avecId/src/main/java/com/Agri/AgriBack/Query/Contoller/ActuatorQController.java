package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Query.entity.ActuatorQ;
import com.Agri.AgriBack.Query.services.ActuatorQService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actuator")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ActuatorQController {
    @Autowired
    private ActuatorQService service;

    @GetMapping
    public ResponseEntity<List<ActuatorQ>> getAllActuators(){
        return ResponseEntity.ok(service.getActuators());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<ActuatorQ>> getActuatorsByUser(@PathVariable String id){
        return ResponseEntity.ok(service.getActuatorsByIdUser(id));
    }

    @GetMapping("/{id}")
    public  ResponseEntity<ActuatorQ> getActuatorById(@PathVariable String id){
        return ResponseEntity.ok(service.getActuatorById(id));
    }
}
