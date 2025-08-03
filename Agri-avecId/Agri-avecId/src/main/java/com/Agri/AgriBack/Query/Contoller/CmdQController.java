package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Query.entity.CommandQ;
import com.Agri.AgriBack.Query.services.CommandQService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/command")
@RequiredArgsConstructor
public class CmdQController {
    @Autowired
    private CommandQService service;

    @GetMapping
    public ResponseEntity<List<CommandQ>> getAllCommands(){
        return ResponseEntity.ok(service.findAllCmds());
    }
}
