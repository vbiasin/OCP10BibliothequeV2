package com.ocp7bibliotheque.bibliothequebook.DAO;


import com.ocp7bibliotheque.bibliothequebook.Entites.Book;
import com.ocp7bibliotheque.bibliothequebook.Entites.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReservationRepository extends JpaRepository<Reservation,Integer> {

    List<Reservation> findByBook(Book book) throws Exception;
}
