package com.example.yellow;

public class UserClass {

    public String name, emailadd, username, startDate, password;

    public UserClass() {}

    public UserClass(String name, String emailadd, String username, String startDate, String password) {
        this.name = name;
        this.emailadd = emailadd;
        this.username = username;
        this.startDate = startDate;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailadd() {
        return emailadd;
    }

    public void setEmailadd(String emailadd) {
        this.emailadd = emailadd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
