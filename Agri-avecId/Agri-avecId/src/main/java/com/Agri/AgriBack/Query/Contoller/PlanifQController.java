package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Query.entity.PlanificationQ;
import com.Agri.AgriBack.Query.services.PlanifQService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/planification")
@RequiredArgsConstructor
public class PlanifQController {
    @Autowired
    private PlanifQService service;

    @GetMapping
    public ResponseEntity<List<PlanificationQ>> getAllPlanifs(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanificationQ> getPlanificationById(@PathVariable String id){
        return ResponseEntity.ok(service.getPlanifById(id));
    }
}
