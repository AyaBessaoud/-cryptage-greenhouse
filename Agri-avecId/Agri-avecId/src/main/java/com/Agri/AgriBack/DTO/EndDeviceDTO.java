package com.Agri.AgriBack.DTO;

import com.Agri.AgriBack.Command.entity.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class EndDeviceDTO {
    private Long id;
    private String codDevice;
    private int nivBat;
    private endDevice.TypeDev type;
    private Long serreId;
    private List<Long> actuatorsIds = new ArrayList<>();
    private List<Long> sensorIds = new ArrayList<>();
    private int PS;
    private int PA;
    private float longt;
    private float lat;

    public int getPS() {
        return PS;
    }

    public void setPS(int PS) {
        this.PS = PS;
    }

    public int getPA() {
        return PA;
    }

    public void setPA(int PA) {
        this.PA = PA;
    }

    public float getLongt() {
        return longt;
    }

    public void setLongt(float longt) {
        this.longt = longt;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodDevice() {
        return codDevice;
    }

    public void setCodDevice(String codDevice) {
        this.codDevice = codDevice;
    }

    public int getNivBat() {
        return nivBat;
    }

    public void setNivBat(int nivBat) {
        this.nivBat = nivBat;
    }

    public endDevice.TypeDev getType() {
        return type;
    }

    public void setType(endDevice.TypeDev type) {
        this.type = type;
    }

    public Long getSerreId() {
        return serreId;
    }

    public void setSerreId(Long serreId) {
        this.serreId = serreId;
    }

    public List<Long> getActuatorsIds() {
        return actuatorsIds;
    }

    public void setActuatorsIds(List<Long> actuatorsIds) {
        this.actuatorsIds = actuatorsIds;
    }

    public List<Long> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<Long> sensorIds) {
        this.sensorIds = sensorIds;
    }

    public EndDeviceDTO() {
    }
}
