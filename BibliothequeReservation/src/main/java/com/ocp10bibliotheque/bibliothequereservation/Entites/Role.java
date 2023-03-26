package com.ocp10bibliotheque.bibliothequereservation.Entites;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;


@Entity
public class Role implements Serializable{

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @ManyToMany(mappedBy = "roles")
    private Collection<UserAccount> userAccounts;


    public Role() {
        super();
        // TODO Auto-generated constructor stub
    }


    public Role(String name) {
        super();
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int getId() {
        return id;
    }

}
