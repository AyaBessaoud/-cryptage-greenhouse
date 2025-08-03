package com.Agri.AgriBack.Command.Controller;

import com.Agri.AgriBack.Command.entity.CmdWEtat;
import com.Agri.AgriBack.Command.entity.WaitCommand;
import com.Agri.AgriBack.Command.services.WCommandCService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send_command")
@RequiredArgsConstructor
public class WCmdController {
    @Autowired
    private WCommandCService service;

    @PostMapping
    public ResponseEntity<WaitCommand> SendCommand(@RequestBody CmdWEtat cmd){
        return ResponseEntity.ok(service.createWCmd(cmd));
    }
}
