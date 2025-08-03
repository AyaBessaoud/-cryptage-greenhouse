package com.Agri.AgriBack.Command.entity;

public class AddEmployee {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String mobile;
    private Employee.Role role;

    public String getFirstName() {
        return firstName;
    }

    public Employee.Role getRole() {
        return role;
    }

    public void setRole(Employee.Role role) {
        this.role = role;
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
}
