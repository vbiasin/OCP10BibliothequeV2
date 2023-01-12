package com.ocp10bibliotheque.bibliothequereservation.DTO;

public class UserAccountDTO {

    private String mail;
    private String lastName;
    private String firstName;


    public UserAccountDTO(String mail, String lastName, String firstName) {
        this.mail = mail;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

}