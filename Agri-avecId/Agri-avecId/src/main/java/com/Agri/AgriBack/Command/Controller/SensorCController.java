package com.Agri.AgriBack.Command.Controller;

import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.entity.endDevice;
import com.Agri.AgriBack.Command.services.SensorCService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sensor")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SensorCController {

    @Autowired
    private SensorCService service;

    @PostMapping
    public ResponseEntity<Sensor> CreateSensor(@RequestBody Sensor sensor){
        return ResponseEntity.ok(service.CreateSensor(sensor));
    }

    @PutMapping(value = "/{id}")     //l'id dans le path va etre passer dans l'argument
    private ResponseEntity<Sensor> updateSensor (@RequestBody Sensor sensor, @PathVariable(name ="id") Long id )
    {
        sensor.setId(id);
        service.UpdateSensor(sensor);
        return ResponseEntity.ok(sensor);
    }

    @DeleteMapping("/{id}")
    public String deleteSensor(@PathVariable Long id) {
        service.deleteSensor(id);
        return "Le capteur avec l'ID " + id + " a été supprimé avec succès.";
    }

}
