package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Query.entity.EmployeeQ;
import com.Agri.AgriBack.Query.services.EmployeeQService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeQController {

    @Autowired
    private EmployeeQService empService;

    @GetMapping
    public ResponseEntity<List<EmployeeQ>> getEmployees() {
        return ResponseEntity.ok(empService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public EmployeeQ getEmployee(@PathVariable String id) {
        return empService.getEmployeeById(id); // Supposons que le service lève déjà l'exception
    }
}
