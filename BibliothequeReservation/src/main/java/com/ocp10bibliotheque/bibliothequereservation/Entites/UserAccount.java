package com.ocp10bibliotheque.bibliothequereservation.Entites;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;


@Entity
public class UserAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date signInDate;
    @OneToOne
    private Contact contact;
    private String mail;
    private String password;
    private Boolean isActive;
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
            name = "userAccounts_roles",
            joinColumns = @JoinColumn(name = "userAccount_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    public UserAccount() {
    }

    public UserAccount(String mail, String password) {
        this.mail = mail;
        this.password = password;
        this.signInDate = new Date();
        this.isActive = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Date getSignInDate() {
        return signInDate;
    }

    public void setSignInDate(Date signInDate) {
        this.signInDate = signInDate;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
