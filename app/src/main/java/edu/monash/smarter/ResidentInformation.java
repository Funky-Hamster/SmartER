package edu.monash.smarter;

public class ResidentInformation {
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public int getNor() {
        return nor;
    }

    public void setNor(int nor) {
        this.nor = nor;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public ResidentInformation(String firstname, String surname, String dob, String address, String postcode, String email, int mobile, int nor, String provider) {
        this.firstname = firstname;
        this.surname = surname;
        this.dob = dob;
        this.address = address;
        this.postcode = postcode;
        this.email = email;
        this.mobile = mobile;
        this.nor = nor;
        this.provider = provider;
    }

    private String firstname = "";
    private String surname = "";
    private String dob = "";
    private String address = "";
    private String postcode = "";
    private String email = "";
    private int mobile = 0;
    private int nor = 0;
    private String provider = "";
}
