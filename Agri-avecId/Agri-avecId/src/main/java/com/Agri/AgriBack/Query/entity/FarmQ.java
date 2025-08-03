package com.Agri.AgriBack.Query.entity;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id", scope = FarmQ.class)
@Document(collection = "Farm")
public class FarmQ {
    @Id
    private String id;
    private String Description;
    @DBRef
    private List<GreenHouseQ> serres = new ArrayList<>();

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

    public List<GreenHouseQ> getSerres() {
        return serres;
    }

    public void setSerres(List<GreenHouseQ> serres) {
        this.serres = serres;
    }

    public FarmQ() {
    }
}
