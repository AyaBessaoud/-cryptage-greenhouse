package com.Agri.AgriBack.Query.entity;

import com.Agri.AgriBack.Command.entity.Actuator;
import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Actuator_Config")
public class ActuatorConfig {
    @Id
    private String id;
    private String idU;
    private String codDevice;
    private String frame;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdU() {
        return idU;
    }

    public void setIdU(String idU) {
        this.idU = idU;
    }

    public String getCodDevice() {
        return codDevice;
    }

    public void setCodDevice(String codDevice) {
        this.codDevice = codDevice;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public ActuatorConfig() {
    }
}
