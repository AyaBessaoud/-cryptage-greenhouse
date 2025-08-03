package com.Agri.AgriBack.Command.Controller;

import com.Agri.AgriBack.Command.entity.Command;
import com.Agri.AgriBack.Command.services.CmdCService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/command")
@RequiredArgsConstructor
public class CmdCController {
    @Autowired
    private CmdCService service;

    @PostMapping
    public ResponseEntity<Command> createCommand (@RequestBody Command command){
        return ResponseEntity.ok(service.createCommand(command));
    }

}
