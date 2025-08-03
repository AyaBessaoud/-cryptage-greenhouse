package com.Agri.AgriBack.DTO;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.entity.endDevice;

import java.util.ArrayList;
import java.util.List;

public class ActuatorDTO {
    private Long id;
    private String idU;
    private int index;
    private String description;
    private float longt;
    private float lat;
    private List<Long> sensorsIds = new ArrayList<>();
    private Long deviceId;
    private Actuator.Outputs output;

    public Actuator.Outputs getOutput() {
        return output;
    }

    public void setOutput(Actuator.Outputs output) {
        this.output = output;
    }

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

    public List<Long> getSensorsIds() {
        return sensorsIds;
    }

    public void setSensorsIds(List<Long> sensorsIds) {
        this.sensorsIds = sensorsIds;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public ActuatorDTO() {
    }
}
