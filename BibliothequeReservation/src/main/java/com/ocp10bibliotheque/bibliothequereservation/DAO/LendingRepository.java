package com.ocp10bibliotheque.bibliothequereservation.DAO;


import com.ocp10bibliotheque.bibliothequereservation.Entites.Book;
import com.ocp10bibliotheque.bibliothequereservation.Entites.Lending;
import com.ocp10bibliotheque.bibliothequereservation.Entites.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LendingRepository extends JpaRepository<Lending,Integer> {

    List<Lending> findByBookIdAndUserAccountMail(int id, String userAccountMail) throws Exception;
    List<Lending> findByBookId(int idBook) throws Exception;
    List<Lending> findByBook(Book book) throws Exception;

}
