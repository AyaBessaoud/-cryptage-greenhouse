package com.Agri.AgriBack.DTO;

import com.Agri.AgriBack.Command.entity.Actuator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class CreatePlanif {
    private String description;
    private String pattern;
    private int etat;
    private List<LocalDate> dates;
    private List<LocalTime> times;
    private List<String> days;
    private List<String> months;
    private Actuator actuator;

    public List<LocalTime> getTimes() {
        return times;
    }

    public void setTimes(List<LocalTime> times) {
        this.times = times;
    }

    public List<String> getMonths() {
        return months;
    }

    public void setMonths(List<String> months) {
        this.months = months;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LocalDate> getDates() {
        return dates;
    }

    public void setDates(List<LocalDate> dates) {
        this.dates = dates;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public Actuator getActuator() {
        return actuator;
    }

    public void setActuator(Actuator actuator) {
        this.actuator = actuator;
    }

    public CreatePlanif() {
    }
}
