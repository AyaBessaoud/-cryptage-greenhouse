package com.Agri.AgriBack.Command.Controller;

import com.Agri.AgriBack.Command.entity.Farm;
import com.Agri.AgriBack.Command.services.FarmCService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/farm")
@RequiredArgsConstructor
public class FarmCController {
    @Autowired
    private FarmCService service;

    @PostMapping
    public ResponseEntity<Farm> Create(@RequestBody Farm farm){
        return ResponseEntity.ok(service.createFarm(farm));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Farm> update(@RequestBody Farm farm, @PathVariable Long id){
        farm.setId(id);
        return ResponseEntity.ok(service.updateFarm(farm));
    }
}
