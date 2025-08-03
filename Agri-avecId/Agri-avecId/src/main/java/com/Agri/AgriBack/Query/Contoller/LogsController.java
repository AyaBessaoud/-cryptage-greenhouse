package com.Agri.AgriBack.Query.Contoller;


import com.Agri.AgriBack.Query.entity.GreenHouseQ;
import com.Agri.AgriBack.Query.entity.Logs;
import com.Agri.AgriBack.Query.repository.GreenHouseQRepo;
import com.Agri.AgriBack.Query.repository.LogsRepo;
import com.Agri.AgriBack.Query.services.LogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogsController {
    @Autowired
    private LogsRepo repo;
    @Autowired
    private LogsService logsService;
    @Autowired
    private GreenHouseQRepo serreRepo;

    @GetMapping("/{code}")
    public ResponseEntity<Map<String, Object>> getLogsByDevicePage(
            @PathVariable String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try{
            List<Logs> logs = new ArrayList<Logs>();
            Pageable paging = PageRequest.of(page, size);

            Page<Logs> pageTuts = repo.findByCodDevice(code , paging);
            logs = pageTuts.getContent();  //to retrieve the List of items in the page.
            if (logs.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);  //renvoies 204
            }
            Map<String, Object> response = new HashMap<>();  //pour inclure les métadonnées
            response.put("logs", logs);
            response.put("currentPage", pageTuts.getNumber()); // for current Page.
            response.put("totalItems", pageTuts.getTotalElements());  //for total items stored in database
            response.put("totalPages", pageTuts.getTotalPages());  //for number of total pages

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/history/{codDevice}")
    public ResponseEntity<Map<String, Object>> getDeviceHistory(@PathVariable String codDevice) {
        return ResponseEntity.ok(logsService.getLatestDeviceData(codDevice));
    }

    @GetMapping("/history/serre/{idSerre}")
    public ResponseEntity<Map<String, Object>> getHistoryBySerre(@PathVariable String idSerre) {
        Optional<GreenHouseQ> serreOpt = serreRepo.findById(idSerre);
        if (serreOpt.isPresent()) {
            return ResponseEntity.ok(logsService.getLatestDataBySerre(serreOpt.get().getId()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
