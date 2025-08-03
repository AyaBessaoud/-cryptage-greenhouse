package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Query.entity.GreenHouseQ;
import com.Agri.AgriBack.Query.services.GreenHouseQService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/greenHouse")
@RequiredArgsConstructor
public class GreenHouseQController {
    @Autowired
    private GreenHouseQService service;

    @GetMapping("/user/{id}")
    public ResponseEntity<List<GreenHouseQ>> getAllGreenHouses(@PathVariable String id){
        return ResponseEntity.ok(service.getAlls(id));
    }
}
