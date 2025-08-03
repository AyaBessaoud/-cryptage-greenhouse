package com.Agri.AgriBack.Query.entity;


import com.Agri.AgriBack.Command.entity.Actuator;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "Actuator_History")
public class ActuatorHistoryQ {
    @Id
    private String id;
    private String idU;
    private int index;
    private Actuator.Outputs output;
    private LocalDateTime date;
    private int etat;
    private String codDevice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdU() {
        return idU;
    }

    public void setIdU(String idU) {
        this.idU = idU;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Actuator.Outputs getOutput() {
        return output;
    }

    public void setOutput(Actuator.Outputs output) {
        this.output = output;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public String getCodDevice() {
        return codDevice;
    }

    public void setCodDevice(String codDevice) {
        this.codDevice = codDevice;
    }


}
