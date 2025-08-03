package com.Agri.AgriBack.Command.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Sensor_Command")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id", scope = Sensor.class)
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String idU;
    private  int index;
    private String description;
    private int alertThersholdD;
    private int normalThersholdD;
    private int alertThersholdN;
    private int normalThersholdN;
    private SensorType typeSensor;
    private float longt;
    private float lat;
    //private boolean scheduled;


    @ManyToMany (mappedBy = "sensors",  cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Actuator> actuators = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "end_device_id") // Clé étrangère dans la table Sensor
    private endDevice endDevice;

    /*public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }*/

    public String getIdU() {
        return idU;
    }

    public void setIdU(String idU) {
        this.idU = idU;
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

    public com.Agri.AgriBack.Command.entity.endDevice getEndDevice() {
        return endDevice;
    }

    public void setEndDevice(com.Agri.AgriBack.Command.entity.endDevice endDevice) {
        this.endDevice = endDevice;
    }

    public List<Actuator> getActuators() {
        return actuators;
    }

    public void setActuators(List<Actuator> actuators) {
        this.actuators = actuators;
    }

    public enum Sensors {
        gaz,
        humidite,
        temperature,
        humSol,
        lumiere
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

    public SensorType getTypeSensor() {
        return typeSensor;
    }

    public void setTypeSensor(SensorType typeSensor) {
        this.typeSensor = typeSensor;
    }


    public Sensor() {
    }
}
