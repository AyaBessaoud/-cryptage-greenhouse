package com.Agri.AgriBack.Query.entity;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "DeviceHistory")
public class DeviceHistory {
    @Id
    private String id;
    private String codDevice;
    private int battery;
    private LocalDateTime date;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodDevice() {
        return codDevice;
    }

    public void setCodDevice(String codDevice) {
        this.codDevice = codDevice;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }
}
