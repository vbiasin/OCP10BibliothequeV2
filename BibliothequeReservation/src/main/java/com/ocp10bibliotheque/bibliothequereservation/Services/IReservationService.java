package com.ocp10bibliotheque.bibliothequereservation.Services;

import com.ocp10bibliotheque.bibliothequereservation.Entites.Book;
import com.ocp10bibliotheque.bibliothequereservation.Entites.Reservation;
import com.ocp10bibliotheque.bibliothequereservation.Entites.UserAccount;

import java.util.List;

public interface IReservationService {

    public Reservation reserve(String userAccountMail, int idBook) throws Exception;
    public List<Reservation> displayReservationByUser(String userAccountMail) throws Exception ;
    public void cancelReservation(int idReservation) throws Exception;
    public void deleteReservation(int idReservation) throws Exception;
    public void validateReservation(int idReservation) throws Exception;

    public void checkCurrentPosition(int idReservation, String userAccountMail) throws Exception;

    public void checkReservationConditionByUser(UserAccount userAccount, Book book) throws Exception;

    public boolean checkFirstReservation(Reservation reservation) throws Exception;
    public void startReservation(Reservation reservation) throws Exception;

}
