package com.ocp10bibliotheque.bibliothequereservation.Services;

import com.ocp10bibliotheque.bibliothequereservation.Entites.Reservation;

import java.util.List;

public interface IReservationService {

    public Reservation reserve(String userAccountMail, int idBook) throws Exception;
    public List<Reservation> displayReservationByUser(String userAccountMail) throws Exception ;
    public void cancelReservation(int idReservation) throws Exception;
    public void deleteReservation(int idReservation) throws Exception;
    public void validateReservation(int idReservation) throws Exception;

    public int checkCurrentPosition(int idReservation) throws Exception;

}
