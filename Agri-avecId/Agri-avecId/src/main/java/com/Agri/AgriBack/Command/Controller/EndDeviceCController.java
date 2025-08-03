package com.Agri.AgriBack.Command.Controller;

import com.Agri.AgriBack.Command.entity.AddDevice;
import com.Agri.AgriBack.Command.entity.endDevice;
import com.Agri.AgriBack.Command.services.EndDeviceCService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/endDevice")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EndDeviceCController {

    @Autowired
    private EndDeviceCService service;

    @PostMapping
    public ResponseEntity<endDevice> createDevice(@RequestBody AddDevice device) {

        endDevice enddevice = new endDevice();
        enddevice.setNivBat(254);
        enddevice.setSensors(new ArrayList<>());
        enddevice.setLocalOutput(new ArrayList<>());
        enddevice.setConfig(device.getConfig());
        enddevice.setType(device.getType());
        enddevice.setSerre(device.getSerre());
        endDevice saved = service.createEndDevice(enddevice);
        return ResponseEntity.ok(saved);
    }

    @PutMapping(value = "/{id}")     //l'id dans le path va etre passer dans l'argument
    private ResponseEntity<endDevice> updateEndDevice (@RequestBody endDevice device, @PathVariable(name ="id") Long id )
    {
        device.setId(id);
        service.updateEndDevice(device);
        return ResponseEntity.ok(device);
    }

    @DeleteMapping("/{id}")
    public String deleteDevice(@PathVariable Long id) {
        service.deleteDevice(id);
        return "L'appareil avec l'ID " + id + " a été supprimé avec succès.";
    }
}
