package com.Agri.AgriBack.Query.entity;

import com.Agri.AgriBack.Command.entity.Sensor;
import com.Agri.AgriBack.Command.entity.SensorType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import com.Agri.AgriBack.Command.entity.endDevice;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id", scope = SensorQ.class)
@Document(collection = "Sensor")
public class SensorQ {
    @Id
    private String id;
    private String idU;
    private  int index;
    private String description;
    private int alertThersholdD;
    private int normalThersholdD;
    private int alertThersholdN;
    private int normalThersholdN;
    private float longt;
    private float lat;
    //private boolean scheduled;
    private SensorType typeSensor;
    @DBRef
    private List<ActuatorQ> actuators = new ArrayList<>();
    //@JsonIgnore
    @DBRef
    private endDeviceQ device;

    /*public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }*/

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

    public String getIdU() {
        return idU;
    }

    public void setIdU(String idU) {
        this.idU = idU;
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

    public List<ActuatorQ> getActuators() {
        return actuators;
    }

    public void setActuators(List<ActuatorQ> actuators) {
        this.actuators = actuators;
    }

    public endDeviceQ getEndDevice() {
        return device;
    }

    public void setEndDevice(endDeviceQ endDevice) {
        this.device = endDevice;
    }

    public SensorType getTypeSensor() {
        return typeSensor;
    }

    public void setTypeSensor(SensorType typeSensor) {
        this.typeSensor = typeSensor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


}
