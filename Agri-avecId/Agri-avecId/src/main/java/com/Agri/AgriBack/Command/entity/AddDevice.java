package com.Agri.AgriBack.Command.entity;

import java.util.ArrayList;
import java.util.List;

public class AddDevice {
    private DeviceConfig config;
    private endDevice.TypeDev type;
    private GreenHouse serre;

    public GreenHouse getSerre() {
        return serre;
    }

    public void setSerre(GreenHouse serre) {
        this.serre = serre;
    }

    public endDevice.TypeDev getType() {
        return type;
    }

    public void setType(endDevice.TypeDev type) {
        this.type = type;
    }

    public DeviceConfig getConfig() {
        return config;
    }

    public void setConfig(DeviceConfig config) {
        this.config = config;
    }

}
