package com.Agri.AgriBack.DTO;

import java.util.List;

public class GreenHouseDTO {
    private Long id;
    private String Description;
    private Long fermeId;
    private List<Long> deviceIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Long getFermeId() {
        return fermeId;
    }

    public void setFermeId(Long fermeId) {
        this.fermeId = fermeId;
    }

    public List<Long> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<Long> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public GreenHouseDTO() {
    }
}
