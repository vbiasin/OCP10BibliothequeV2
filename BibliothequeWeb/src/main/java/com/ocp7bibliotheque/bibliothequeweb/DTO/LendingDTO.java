package com.ocp7bibliotheque.bibliothequeweb.DTO;

public class LendingDTO {

    private String mail;
    private int idBook;

    public LendingDTO(String mail, int idBook) {
        this.mail = mail;
        this.idBook = idBook;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getIdBook() {
        return idBook;
    }

    public void setIdBook(int idBook) {
        this.idBook = idBook;
    }
}
