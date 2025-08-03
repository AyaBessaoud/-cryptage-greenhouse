package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Query.entity.FarmQ;
import com.Agri.AgriBack.Query.services.FarmQService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/farm")
@RequiredArgsConstructor
public class FarmQController {
    @Autowired
    private FarmQService service;

    @GetMapping("/user/{id}")
    public ResponseEntity<List<FarmQ>> getAlls(@PathVariable String id){
        return ResponseEntity.ok(service.getAllFarms(id));
    }
}
