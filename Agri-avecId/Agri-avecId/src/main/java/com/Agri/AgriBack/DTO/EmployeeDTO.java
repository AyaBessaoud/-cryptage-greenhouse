package com.Agri.AgriBack.DTO;

import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.Query.entity.GreenHouseQ;

import java.util.List;

public class EmployeeDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String mobile;
    private Employee.Role role;
    private List<Long> serreId;
    private List<Long> fermeId;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Employee.Role getRole() {
        return role;
    }

    public void setRole(Employee.Role role) {
        this.role = role;
    }

    public List<Long> getSerreId() {
        return serreId;
    }

    public void setSerreId(List<Long> serreId) {
        this.serreId = serreId;
    }

    public List<Long> getFermeId() {
        return fermeId;
    }

    public void setFermeId(List<Long> fermeId) {
        this.fermeId = fermeId;
    }

    public EmployeeDTO() {
    }
}
