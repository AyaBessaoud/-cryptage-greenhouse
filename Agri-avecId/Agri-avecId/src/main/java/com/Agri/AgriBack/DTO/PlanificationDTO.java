package com.Agri.AgriBack.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class PlanificationDTO {
    private Long id;
    private String description;
    private boolean blocked;
    private List<LocalDate> dates;
    private List<LocalTime> times;
    private List<String> days;
    private List<String> months;
    private String pattern;
    private String frame;
    private int etat;
    private LocalDateTime creationDate;
    private Long actuatorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public List<LocalDate> getDates() {
        return dates;
    }

    public void setDates(List<LocalDate> dates) {
        this.dates = dates;
    }

    public List<LocalTime> getTimes() {
        return times;
    }

    public void setTimes(List<LocalTime> times) {
        this.times = times;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public List<String> getMonths() {
        return months;
    }

    public void setMonths(List<String> months) {
        this.months = months;
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

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getActuatorId() {
        return actuatorId;
    }

    public void setActuatorId(Long actuatorId) {
        this.actuatorId = actuatorId;
    }

    public PlanificationDTO() {
    }
}
