package com.Agri.AgriBack.DTO;

import com.Agri.AgriBack.Command.entity.Actuator;

import java.time.LocalDateTime;

public class ActuatorHistoryDTO {
    private String idU;
    private int index;
    private String output;
    private LocalDateTime date;
    private int etat;
    private String codDevice;
    private String description;

    public String getIdU() {
        return idU;
    }

    public void setIdU(String idU) {
        this.idU = idU;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public String getCodDevice() {
        return codDevice;
    }

    public void setCodDevice(String codDevice) {
        this.codDevice = codDevice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActuatorHistoryDTO() {
    }
}
