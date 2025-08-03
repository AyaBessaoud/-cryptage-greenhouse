package com.Agri.AgriBack.Command.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Output_Command")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id", scope = Actuator.class)
public class Actuator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String idU;
    private int index;
    private String description;
    private float longt;
    private float lat;
    @ManyToMany
    @JoinTable(
            name = "sensor_actuator",
            joinColumns = @JoinColumn(name = "actuator_id"),
            inverseJoinColumns = @JoinColumn(name = "sensor_id")
    )
    private List<Sensor> sensors = new ArrayList<>();
    private Outputs output;
    @ManyToOne
    @JoinColumn(name = "end_device_id")
    private endDevice device;


    public enum Outputs{
        Lampe,
        Ventilateur,
        Pompe
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
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


    public Outputs getOutput() {
        return output;
    }

    public void setOutput(Outputs output) {
        this.output = output;
    }

    public endDevice getDevice() {
        return device;
    }

    public void setDevice(endDevice device) {
        this.device = device;
    }

    public Actuator() {
    }
}
