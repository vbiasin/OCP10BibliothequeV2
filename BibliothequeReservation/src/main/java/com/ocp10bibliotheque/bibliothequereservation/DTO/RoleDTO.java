package com.ocp10bibliotheque.bibliothequereservation.DTO;

public class RoleDTO {

    private String mail;
    private int idRole;

    public RoleDTO(String mail, int idRole) {
        this.mail = mail;
        this.idRole = idRole;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }
}