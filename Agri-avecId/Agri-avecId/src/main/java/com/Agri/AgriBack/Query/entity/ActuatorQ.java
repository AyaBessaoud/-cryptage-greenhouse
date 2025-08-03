package com.Agri.AgriBack.Query.entity;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id", scope = ActuatorQ.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "Output")
public class ActuatorQ {
    @Id
    private String id;
    private String idU;
    private int index;
    private String description;
    @DBRef
    private List<SensorQ> sensors;
    private Actuator.Outputs output;
    @DBRef
    private endDeviceQ device;
    private float longt;
    private float lat;

    public List<SensorQ> getSensors() {
        return sensors;
    }

    public void setSensors(List<SensorQ> sensors) {
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



    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Actuator.Outputs getOutput() {
        return output;
    }

    public void setOutput(Actuator.Outputs output) {
        this.output = output;
    }

    public endDeviceQ getDevice() {
        return device;
    }

    public void setDevice(endDeviceQ device) {
        this.device = device;
    }
}
