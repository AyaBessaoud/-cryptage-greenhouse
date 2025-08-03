package com.Agri.AgriBack.Query.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id", scope = GreenHouseQ.class)
@Document(collection = "GreenHouse")
public class GreenHouseQ {
    @Id
    private String id;
    private String Description;
    @DBRef
    private FarmQ ferme;
    @DBRef
    private List<endDeviceQ> devices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public FarmQ getFerme() {
        return ferme;
    }

    public void setFerme(FarmQ ferme) {
        this.ferme = ferme;
    }

    public List<endDeviceQ> getDevices() {
        return devices;
    }

    public void setDevices(List<endDeviceQ> devices) {
        this.devices = devices;
    }

    public GreenHouseQ() {
    }
}
