package com.freeminder.saralam.utils;

/**
 * Created by Sasikumar Reddy on 07-09-2015.
 */
public class CustomerModel {

    String Id;
    String Name;
    String Number;
    String Email;
    String Service;
    boolean isChecked;


    public CustomerModel(String id, String name, String number, String email, String service, boolean isChecked) {
        Id = id;
        Name = name;
        Number = number;
        Email = email;
        Service = service;
        this.isChecked = isChecked;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getService() {
        return Service;
    }

    public void setService(String service) {
        Service = service;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

}
