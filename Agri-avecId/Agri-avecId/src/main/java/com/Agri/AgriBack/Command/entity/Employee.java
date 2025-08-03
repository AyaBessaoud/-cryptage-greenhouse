package com.Agri.AgriBack.Command.entity;

import com.Agri.AgriBack.Query.entity.EmployeeQ;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Employee_Command")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String mobile;
    private Role role;
    @ManyToMany
    @JoinTable(
            name = "employee_greenhouses",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "greenhouse_id")
    )
    private List<GreenHouse> serre;
    @ManyToMany
    @JoinTable(
            name = "employee_farms",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "farm_id")
    )
    private List<Farm> ferme;

    public List<GreenHouse> getSerre() {
        return serre;
    }

    public void setSerre(List<GreenHouse> serre) {
        this.serre = serre;
    }

    public List<Farm> getFerme() {
        return ferme;
    }

    public void setFerme(List<Farm> ferme) {
        this.ferme = ferme;
    }

    public enum Role {
        Admin,
        Agriculteur
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public Employee(){}
}
