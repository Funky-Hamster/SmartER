package edu.monash.smarter;

public class UserCredentials {
    private String username;
    private String password;
    private String registrationDate;

    public UserCredentials(String username, String password, String registrationDate) {
        this.username = username;
        this.password = password;
        this.registrationDate = registrationDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }



}
