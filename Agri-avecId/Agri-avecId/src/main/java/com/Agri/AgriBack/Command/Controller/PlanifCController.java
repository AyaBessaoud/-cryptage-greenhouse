package com.Agri.AgriBack.Command.Controller;

import com.Agri.AgriBack.Command.entity.Planification;
import com.Agri.AgriBack.Command.services.PlanifCService;
import com.Agri.AgriBack.DTO.CreatePlanif;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/planification")
@RequiredArgsConstructor
public class PlanifCController {
    @Autowired
    private PlanifCService service;

    @PostMapping
    public ResponseEntity<Planification> Create(@RequestBody CreatePlanif planif){
        return ResponseEntity.ok(service.CreatePlanif(planif));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Planification> Update(@PathVariable Long id, @RequestBody Planification planif){
        planif.setId(id);
        Planification updatedPlanif = service.updatePlanif(planif);
        return  ResponseEntity.ok(updatedPlanif);
    }
}
