package com.ocp7bibliotheque.bibliothequebatchmail.DAO;


import com.ocp7bibliotheque.bibliothequebatchmail.Entites.Book;
import com.ocp7bibliotheque.bibliothequebatchmail.Entites.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ReservationRepository extends JpaRepository<Reservation,Integer> {

    Optional<Reservation> findByBookAndStatusOrderById(Book book, String status) throws Exception;
    List<Reservation> findByStatus(String status) throws Exception;


}
