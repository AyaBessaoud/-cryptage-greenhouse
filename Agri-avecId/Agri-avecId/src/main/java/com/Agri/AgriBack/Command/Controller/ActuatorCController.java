package com.Agri.AgriBack.Command.Controller;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.services.ActuatorCService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/actuator")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ActuatorCController {
    @Autowired
    private ActuatorCService service;

    @PostMapping
    public ResponseEntity<Actuator> CreateActuator(@RequestBody Actuator actuator){
        return ResponseEntity.ok(service.createActuator(actuator));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Actuator> UpdateActuator(@RequestBody Actuator actuator, @PathVariable Long id){
        actuator.setId(id);
        service.updateActuator(actuator);
        return ResponseEntity.ok(actuator);
    }

    @DeleteMapping("/{id}")
    public String deleteActuator(@PathVariable Long id) {
        service.deleteActuator(id);
        return "L'actionneur avec l'ID " + id + " a été supprimé avec succès.";
    }
}
