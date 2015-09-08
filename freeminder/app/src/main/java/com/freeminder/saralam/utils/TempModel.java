package com.freeminder.saralam.utils;

/**
 * Created by Sasikumar Reddy on 07-09-2015.
 */
public class TempModel {

    String Name;
    String Number;
    String Email;
    String Service;
    boolean isChecked = false;

    public TempModel(String Name, String Number, String Email, String Service, boolean isChecked){

        this.Name= Name;
        this.Number =Number;
        this.Email = Email;
        this.Service =Service;
        this.isChecked =isChecked;

    }


    public void setName(String name) {
        Name = name;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setService(String service) {
        Service = service;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }


    public String getName() {
        return Name;
    }

    public String getNumber() {
        return Number;
    }

    public String getEmail() {
        return Email;
    }

    public String getService() {
        return Service;
    }
    public boolean isIsChecked(){
        return isChecked;
    }



}
