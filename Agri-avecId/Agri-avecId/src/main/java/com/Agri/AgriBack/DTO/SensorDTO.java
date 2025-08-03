package com.Agri.AgriBack.DTO;

import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.entity.SensorType;

import java.util.ArrayList;
import java.util.List;

public class SensorDTO {
    private Long id;
    private String idU;
    private  int index;
    private String description;
    private int alertThersholdD;
    private int normalThersholdD;
    private int alertThersholdN;
    private int normalThersholdN;
    private float longt;
    private float lat;
    private SensorType typeSensor;
    private Long deviceId;
    //private boolean scheduled;
    private List<Long> actuatorsIds = new ArrayList<>();

    /*public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAlertThersholdD() {
        return alertThersholdD;
    }

    public void setAlertThersholdD(int alertThersholdD) {
        this.alertThersholdD = alertThersholdD;
    }

    public int getNormalThersholdD() {
        return normalThersholdD;
    }

    public void setNormalThersholdD(int normalThersholdD) {
        this.normalThersholdD = normalThersholdD;
    }

    public int getAlertThersholdN() {
        return alertThersholdN;
    }

    public void setAlertThersholdN(int alertThersholdN) {
        this.alertThersholdN = alertThersholdN;
    }

    public int getNormalThersholdN() {
        return normalThersholdN;
    }

    public void setNormalThersholdN(int normalThersholdN) {
        this.normalThersholdN = normalThersholdN;
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

    public SensorType getTypeSensor() {
        return typeSensor;
    }

    public void setTypeSensor(SensorType typeSensor) {
        this.typeSensor = typeSensor;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public List<Long> getActuatorsIds() {
        return actuatorsIds;
    }

    public void setActuatorsIds(List<Long> actuatorsIds) {
        this.actuatorsIds = actuatorsIds;
    }

    public SensorDTO() {
    }
}
