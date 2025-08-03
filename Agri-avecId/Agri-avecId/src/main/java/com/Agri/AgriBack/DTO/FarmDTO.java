package com.Agri.AgriBack.DTO;

import com.Agri.AgriBack.Command.entity.GreenHouse;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

public class FarmDTO {
    private Long id;
    private String Description;
    private List<Long> serresIds ;

    public FarmDTO() {
    }

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

    public List<Long> getSerresIds() {
        return serresIds;
    }

    public void setSerresIds(List<Long> serresIds) {
        this.serresIds = serresIds;
    }
}
