package com.Agri.AgriBack.Query.consumer;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.CmdWEtat;
import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.entity.WaitCommand;
import com.Agri.AgriBack.Command.mapper.WaitCmdCToQ;
import com.Agri.AgriBack.Query.entity.ActuatorQ;
import com.Agri.AgriBack.Query.entity.DoAConfig;
import com.Agri.AgriBack.Query.entity.GetDataConfig;
import com.Agri.AgriBack.Query.entity.WaitCommantQ;
import com.Agri.AgriBack.Query.repository.ActuatorQRepo;
import com.Agri.AgriBack.Query.repository.DoAConfigRepo;
import com.Agri.AgriBack.Query.repository.GetDConfigRepo;
import com.Agri.AgriBack.Query.repository.WaitCmdQRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WaitCmdConsumer {
    @Autowired
    private WaitCmdCToQ cmdMapper;
    @Autowired
    private WaitCmdQRepo repo;
    @Autowired
    private GetDConfigRepo dataRepo;
    @Autowired
    private DoAConfigRepo actionRepo;
    @Autowired
    private ActuatorQRepo actRepo;

    @KafkaListener(topics = "WaitCommand_created", groupId = "Agri-group")
    public WaitCommantQ CreateEvent (WaitCommand command){
        WaitCommantQ cmd = cmdMapper.mapper(command);
        if("GETD".equals(cmd.getCommand())){
            GetDataConfig config = new GetDataConfig();
            config.setCodDevice(command.getCodDevice());
            String frame = GetDCommand(command.getCodDevice());
            config.setFrame(frame);
             dataRepo.save(config);
        }
        return repo.save(cmd);
    }

    @KafkaListener(topics = "GetData_created", groupId = "Agri-group")
    public WaitCommantQ CreateDataConfig (CmdWEtat command){
        DoAConfig action = new DoAConfig();
        action.setCodDevice(command.getCodDevice());
        action.setIdU(command.getIdU());
        String frame;
        Optional<ActuatorQ> actOpt = actRepo.findByIdU(command.getIdU());
        if(actOpt.isPresent()){
            ActuatorQ act = actOpt.get();
            frame = DoACommand(command.getCodDevice(),act.getOutput(),command.getEtat(),act.getIndex());
            action.setFrame(frame);
        }
       actionRepo.save(action);

        WaitCommantQ cmd = new WaitCommantQ();
        cmd.setIdU(command.getIdU());
        cmd.setCommand(command.getCommand());
        cmd.setCodDevice(command.getCodDevice());
        WaitCommantQ saved = repo.save(cmd);
        return saved ;
    }

    public String GetDCommand(String codDevice) {
        return String.format("3F;%s;", codDevice);
    }

    public String DoACommand(String codDevice, Actuator.Outputs output, int etat, int index) {
        return String.format("3C;%s;%s;%d;%d;", codDevice,output, etat, index);
    }
}
