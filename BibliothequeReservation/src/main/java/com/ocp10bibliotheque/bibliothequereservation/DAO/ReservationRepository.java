package com.ocp10bibliotheque.bibliothequereservation.DAO;

import com.ocp10bibliotheque.bibliothequereservation.Entites.Book;
import com.ocp10bibliotheque.bibliothequereservation.Entites.Reservation;
import com.ocp10bibliotheque.bibliothequereservation.Entites.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation,Integer> {

    Optional<Reservation> findById(int idReservation) throws Exception;
    List<Reservation> findByUserAccount(UserAccount userAccount) throws Exception;

    List<Reservation> findByBook (Book book) throws Exception;

}
