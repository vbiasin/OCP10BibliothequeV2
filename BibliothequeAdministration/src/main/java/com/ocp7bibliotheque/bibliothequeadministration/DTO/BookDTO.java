package com.ocp7bibliotheque.bibliothequeadministration.DTO;

import com.ocp7bibliotheque.bibliothequeadministration.Entites.Book;

import java.io.Serializable;

public class BookDTO implements Serializable {

    private int idLibrary;
    private int numberExemplar; // selon le cas Total ou Actual
    private int idBook;
    private String author;
    private String title;
    private Book book;

    public BookDTO() {
    }

    public BookDTO(int idLibrary, Book book) {
        this.idLibrary = idLibrary;
        this.book = book;
    }

    public BookDTO(int idBook, int numberExemplar) {
        this.numberExemplar = numberExemplar;
        this.idBook = idBook;
    }

    public BookDTO(String author, String title) {
        this.author = author;
        this.title = title;
    }

    public int getIdLibrary() {
        return idLibrary;
    }

    public void setIdLibrary(int idLibrary) {
        this.idLibrary = idLibrary;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }



    public int getIdBook() {
        return idBook;
    }

    public void setIdBook(int idBook) {
        this.idBook = idBook;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumberExemplar() {
        return numberExemplar;
    }

    public void setNumberExemplar(int numberExemplar) {
        this.numberExemplar = numberExemplar;
    }
}
