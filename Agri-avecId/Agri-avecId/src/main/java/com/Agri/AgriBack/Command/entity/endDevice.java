package com.Agri.AgriBack.Command.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "EndDevice_Command")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les références Hibernate inutiles
//@EqualsAndHashCode(exclude = { "sensors"}) // This,
//@ToString(exclude = { "sensors"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id", scope = endDevice.class)
public class endDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String codDevice;
    private int nivBat;
    private TypeDev type;
    @Embedded
    private DeviceConfig config;   /// orphanRemoval = true
    @OneToMany(mappedBy = "device",  cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = false )
    private List<Actuator> localActuators = new ArrayList<>();

    //@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @OneToMany(mappedBy = "endDevice",  cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = false)
    //@JsonIgnoreProperties({"sensors"})
    private List<Sensor> sensors = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "greenHouse_id")
    private GreenHouse serre;

    public enum TypeDev {
        STM32,
        LoRa_E5,
        ESP32,
        Arduino
    }

    public TypeDev getType() {
        return type;
    }

    public void setType(TypeDev type) {
        this.type = type;
    }

    public DeviceConfig getConfig() {
        return config;
    }

    public void setConfig(DeviceConfig config) {
        this.config = config;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public List<Actuator> getLocalOutput() {
        return localActuators;
    }

    public void setLocalOutput(List<Actuator> localOutput) {
        this.localActuators = localOutput;
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

    public List<Actuator> getLocalActuators() {
        return localActuators;
    }

    public void setLocalActuators(List<Actuator> localActuators) {
        this.localActuators = localActuators;
    }

    public GreenHouse getSerre() {
        return serre;
    }

    public void setSerre(GreenHouse serre) {
        this.serre = serre;
    }

    public endDevice() {
    }
}
