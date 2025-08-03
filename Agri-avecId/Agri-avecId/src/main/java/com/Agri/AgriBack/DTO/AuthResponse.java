package com.Agri.AgriBack.DTO;

import com.Agri.AgriBack.Command.entity.Employee;

public class AuthResponse {
    private String token;
    private Employee employee;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public AuthResponse(String token, Employee employee) {
        this.token = token;
        this.employee = employee;
    }
}
