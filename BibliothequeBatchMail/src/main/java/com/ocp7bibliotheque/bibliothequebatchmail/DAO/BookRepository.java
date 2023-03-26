package com.ocp7bibliotheque.bibliothequebatchmail.DAO;


import com.ocp7bibliotheque.bibliothequebatchmail.Entites.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BookRepository extends JpaRepository<Book,Integer> {



}
