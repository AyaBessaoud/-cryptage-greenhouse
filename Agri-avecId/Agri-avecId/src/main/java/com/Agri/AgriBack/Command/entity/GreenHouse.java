package com.Agri.AgriBack.Command.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "GreenHouse_Command")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id", scope = GreenHouse.class)
public class GreenHouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String Description;
    @ManyToOne
    @JoinColumn(name = "farm_id")
    private Farm ferme;
    @OneToMany(mappedBy = "serre",  cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = false)
    private List<endDevice> devices;

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

    public Farm getFerme() {
        return ferme;
    }

    public void setFerme(Farm ferme) {
        this.ferme = ferme;
    }

    public List<endDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<endDevice> devices) {
        this.devices = devices;
    }

    public GreenHouse() {
    }
}
