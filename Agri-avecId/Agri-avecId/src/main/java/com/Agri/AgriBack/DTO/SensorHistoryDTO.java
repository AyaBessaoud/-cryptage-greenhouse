package com.Agri.AgriBack.DTO;

import com.Agri.AgriBack.Command.entity.SensorType;

import java.time.LocalDateTime;

public class SensorHistoryDTO {
    private String idU;
    private  int index;
    private float value;
    private int etat;
    private String codDevice;
    private SensorType typeSensor;
    private LocalDateTime date;
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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
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

    public SensorType getTypeSensor() {
        return typeSensor;
    }

    public void setTypeSensor(SensorType typeSensor) {
        this.typeSensor = typeSensor;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SensorHistoryDTO() {
    }
}
