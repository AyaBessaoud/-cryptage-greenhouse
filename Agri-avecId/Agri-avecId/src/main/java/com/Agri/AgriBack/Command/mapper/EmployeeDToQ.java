package com.Agri.AgriBack.Command.mapper;

import com.Agri.AgriBack.DTO.EmployeeDTO;
import com.Agri.AgriBack.Query.entity.EmployeeQ;
import com.Agri.AgriBack.Query.entity.FarmQ;
import com.Agri.AgriBack.Query.entity.GreenHouseQ;
import com.Agri.AgriBack.Query.repository.FarmQRepo;
import com.Agri.AgriBack.Query.repository.GreenHouseQRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class EmployeeDToQ {
    @Autowired
    private GreenHouseQRepo serreRepo;
    @Autowired
    private FarmQRepo farmRepo;
    public EmployeeQ mapperDto(EmployeeDTO dto){
        EmployeeQ emp = new EmployeeQ();
        emp.setRole(dto.getRole());
        emp.setId(dto.getId().toString());
        emp.setAddress(dto.getAddress());
        emp.setPassword(dto.getPassword());
        emp.setMobile(dto.getMobile());
        emp.setLastName(dto.getLastName());
        emp.setFirstName(dto.getFirstName());
        emp.setEmail(dto.getEmail());
        if (dto.getFermeId() != null && !dto.getFermeId().isEmpty()) {
            List<FarmQ> fermes = dto.getFermeId().stream()
                    .map(id -> farmRepo.findById(id.toString()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            emp.setFerme(fermes);
        } else {
            emp.setFerme(null);
        }

        if (dto.getSerreId() != null && !dto.getSerreId().isEmpty()) {
            List<GreenHouseQ> serres = dto.getSerreId().stream()
                    .map(id -> serreRepo.findById(id.toString()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            emp.setSerre(serres);
        } else {
            emp.setSerre(null);
        }

        return emp;
    }
}
