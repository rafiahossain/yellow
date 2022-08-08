package com.example.yellow;

public class UserClass {

    public String name, emailadd, username, startDate, password;

    public UserClass() {
        //Empty public constructor, Does not return anything
        //To create an empty object of this class
    }

    public UserClass(String name, String emailadd, String username, String startDate, String password) {
        this.name = name;
        this.emailadd = emailadd;
        this.username = username;
        this.startDate = startDate;
        this.password = password;
    }
}
