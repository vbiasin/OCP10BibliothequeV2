package com.ocp7bibliotheque.bibliothequebook.Entites;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    private Library library;
    private Boolean isAvailable;
    private Date publicationDate;
    private String resume;
    private String title;
    private String author;
    private int numberExemplarTotal;
    private int numberExemplarActual;
    private int currentNumberReservation;
    private int numberMaxReservation;
    @ManyToOne (fetch=FetchType.LAZY)
    private Lending lending;
    @ManyToOne (fetch=FetchType.LAZY)
    private Reservation reservation;


    public Book() {
    }

    public Book(Date publicationDate, String resume, String title, String author, int numberExemplarTotal) {
        this.isAvailable = true;
        this.publicationDate = publicationDate;
        this.resume = resume;
        this.title = title;
        this.author = author;
        this.numberExemplarTotal = numberExemplarTotal;
        this.numberExemplarActual= numberExemplarTotal;
        this.currentNumberReservation=0;
        this.numberMaxReservation=2*numberExemplarTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Lending getLending() {
        return lending;
    }

    public void setLending(Lending lending) {
        this.lending = lending;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public int getNumberExemplarTotal() {
        return numberExemplarTotal;
    }

    public void setNumberExemplarTotal(int numberExemplarTotal) {
        this.numberExemplarTotal = numberExemplarTotal;
    }

    public int getNumberExemplarActual() {
        return numberExemplarActual;
    }

    public void setNumberExemplarActual(int numberExemplarActual) {
        this.numberExemplarActual = numberExemplarActual;
    }

    public int getCurrentNumberReservation() {
        return currentNumberReservation;
    }

    public void setCurrentNumberReservation(int currentNumberReservation) {
        this.currentNumberReservation = currentNumberReservation;
    }
}

