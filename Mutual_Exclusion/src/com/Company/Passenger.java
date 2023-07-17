package com.Company;

class Passenger {
    private String userName;
    private String password;

    Passenger(String userName, String password) {
        this.password = password;
        this.userName = userName;
    }

    public boolean authenticate(String userName, String password) {
        return this.userName.equals(userName) && this.password.equals(password);
    }
}
