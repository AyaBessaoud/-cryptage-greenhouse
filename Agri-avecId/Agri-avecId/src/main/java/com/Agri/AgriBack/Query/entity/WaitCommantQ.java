package com.Agri.AgriBack.Query.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Wait_Command")
public class WaitCommantQ {
    @Id
    private String id;
    private String codDevice;
    private String idU;
    private  String command;
    private String status = "pending";
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodDevice() {
        return codDevice;
    }

    public void setCodDevice(String codDevice) {
        this.codDevice = codDevice;
    }

    public String getIdU() {
        return idU;
    }

    public void setIdU(String idU) {
        this.idU = idU;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public WaitCommantQ() {
    }
}
