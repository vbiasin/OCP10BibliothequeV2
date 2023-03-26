package com.ocp10bibliotheque.bibliothequereservation.DTO;

public class ReservationDTO {
    private String mail;
    private int idBook;

    public ReservationDTO() {
    }

    public ReservationDTO(String mail, int idBook) {
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
