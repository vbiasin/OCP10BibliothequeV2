package com.ocp10bibliotheque.bibliothequereservation.DAO;


import com.ocp10bibliotheque.bibliothequereservation.Entites.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BookRepository extends JpaRepository<Book,Integer> {

    Optional<Book> findById(int idBook);
    public List<Book> findByTitleContainsOrAuthorContains(String title, String author);

}
