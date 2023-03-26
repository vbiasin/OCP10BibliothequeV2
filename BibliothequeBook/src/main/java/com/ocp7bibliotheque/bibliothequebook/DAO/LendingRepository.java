package com.ocp7bibliotheque.bibliothequebook.DAO;

import com.ocp7bibliotheque.bibliothequebook.Entites.Book;
import com.ocp7bibliotheque.bibliothequebook.Entites.Lending;
import com.ocp7bibliotheque.bibliothequebook.Entites.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface LendingRepository extends JpaRepository<Lending,Integer> {

    Optional<Lending> findById(int idLending) throws Exception;
    List<Lending> findByUserAccount(UserAccount userAccount) throws Exception;
    List<Lending> findByBook(Book book) throws Exception;
    List<Lending>findByBookOrderByEndDate(Book book) throws Exception;

}
