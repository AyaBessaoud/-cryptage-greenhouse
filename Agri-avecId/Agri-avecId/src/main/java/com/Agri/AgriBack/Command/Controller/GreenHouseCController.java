package com.Agri.AgriBack.Command.Controller;

import com.Agri.AgriBack.Command.entity.GreenHouse;
import com.Agri.AgriBack.Command.services.GreenHouseCService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/greenHouse")
@RequiredArgsConstructor
public class GreenHouseCController {
    @Autowired
    private GreenHouseCService service;

    @PostMapping
    public ResponseEntity<GreenHouse> create(@RequestBody GreenHouse serre){
       return ResponseEntity.ok(service.createGreenHouse(serre));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<GreenHouse> update(@RequestBody GreenHouse serre, @PathVariable Long id){
        serre.setId(id);
        return ResponseEntity.ok(service.updateGreenHouse(serre));
    }
}
