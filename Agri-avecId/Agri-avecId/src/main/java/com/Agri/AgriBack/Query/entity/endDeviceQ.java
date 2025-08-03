package com.Agri.AgriBack.Query.entity;

import com.Agri.AgriBack.Command.entity.endDevice;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
//@EqualsAndHashCode(callSuper = true)
@EqualsAndHashCode(exclude = { "sensors"}) // This,
@ToString(exclude = { "sensors"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id", scope = endDeviceQ.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "EndDevice")
public class endDeviceQ {
    @Id
    private String id;
    private String codDevice;
    private int nivBat;
    private DeviceConfigQ config;
    private endDevice.TypeDev type;

    @DBRef
    private List<SensorQ> sensors ;
    @DBRef
    private List<ActuatorQ> localActuators = new ArrayList<>();
    @DBRef
    private GreenHouseQ serre;

    public endDevice.TypeDev getType() {
        return type;
    }

    public void setType(endDevice.TypeDev type) {
        this.type = type;
    }

    public DeviceConfigQ getConfig() {
        return config;
    }

    public void setConfig(DeviceConfigQ config) {
        this.config = config;
    }

    public List<SensorQ> getSensors() {
        return sensors;
    }

    public void setSensors(List<SensorQ> sensors) {
        this.sensors = sensors;
    }

    public List<ActuatorQ> getlocalActuators() {
        return localActuators;
    }

    public void setlocalActuators(List<ActuatorQ> localOutput) {
        this.localActuators = localOutput;
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

    public int getNivBat() {
        return nivBat;
    }

    public void setNivBat(int nivBat) {
        this.nivBat = nivBat;
    }

    public GreenHouseQ getSerre() {
        return serre;
    }

    public void setSerre(GreenHouseQ serre) {
        this.serre = serre;
    }


}
