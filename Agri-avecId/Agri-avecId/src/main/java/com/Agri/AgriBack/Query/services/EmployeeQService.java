package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Query.entity.EmployeeQ;
import com.Agri.AgriBack.Query.repository.EmployeeQRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeQService {

    @Autowired
    private EmployeeQRepo repo;

    public List<EmployeeQ> getAllEmployees() {
        return repo.findAll();
    }

    public EmployeeQ getEmployeeById(String _id) {
        Optional<EmployeeQ> emp = repo.findById(_id);
        return emp.orElse(null); // Retourne null si non trouvé
    }
}
