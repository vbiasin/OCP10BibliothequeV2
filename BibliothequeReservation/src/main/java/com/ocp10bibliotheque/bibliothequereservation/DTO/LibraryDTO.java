package com.ocp10bibliotheque.bibliothequereservation.DTO;

import java.io.Serializable;

public class LibraryDTO implements Serializable {

    private String name;
    private String address;
    private int pages;
    private int size;

    public LibraryDTO(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}

