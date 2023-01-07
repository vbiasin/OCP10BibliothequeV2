package com.ocp10bibliotheque.bibliothequereservation.Entites;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Lending implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isExtensible;
    @OneToOne
    private UserAccount userAccount;
    @OneToOne
    private Book book;

    public Lending() {
    }

    public Lending(UserAccount userAccount, Book book) {
        this.status = "en cours";
        this.isExtensible=true;
        this.userAccount = userAccount;
        this.book = book;
        this.startDate = LocalDateTime.now();
        this.endDate = startDate.plusDays(28);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public boolean isExtensible() {
        return isExtensible;
    }

    public void setExtensible(boolean extensible) {
        isExtensible = extensible;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}