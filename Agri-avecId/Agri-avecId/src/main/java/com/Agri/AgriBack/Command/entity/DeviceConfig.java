package com.Agri.AgriBack.Command.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class DeviceConfig {
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

    public DeviceConfig() {
    }
}
