package com.Agri.AgriBack.Query.entity;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Notification")
public class Notification {
    @Id
    private String id;
    @DBRef
    private List<EmployeeQ> users;
    private String message;

    public List<EmployeeQ> getUsers() {
        return users;
    }

    public void setUsers(List<EmployeeQ> users) {
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Notification() {
    }
}
