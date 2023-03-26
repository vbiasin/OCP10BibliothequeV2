package com.ocp10bibliotheque.bibliothequereservation.Controller;

import com.ocp10bibliotheque.bibliothequereservation.DTO.ReservationDTO;
import com.ocp10bibliotheque.bibliothequereservation.Entites.Reservation;
import com.ocp10bibliotheque.bibliothequereservation.Services.IReservationService;
import com.ocp10bibliotheque.bibliothequereservation.Services.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReservationController {

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IReservationService reservationService;

    @GetMapping("/reservationsBack")
    public ResponseEntity<Boolean> checkActiveUserContact(@RequestParam("activeUsername")  String activeUserName) throws Exception {
        try {
            return new ResponseEntity<Boolean>(userAccountService.checkUserAccountContact(activeUserName), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/reserveBack")
    public ResponseEntity<Reservation>reserve(@RequestBody ReservationDTO reservationDTO) throws Exception {
        try {
            return new ResponseEntity<>(reservationService.reserve(reservationDTO.getMail(),reservationDTO.getIdBook()), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/displayReservationsBack")
    public ResponseEntity<List<Reservation>> displayReservations(@RequestParam("activeUsername")  String activeUserName) throws Exception {
        try {
            return new ResponseEntity<>(reservationService.displayReservationByUser(activeUserName), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/cancelReservationBack")
    public ResponseEntity<String>cancelReservation(@RequestBody int idReservation) throws Exception {
        try {
            reservationService.cancelReservation(idReservation);
            return new ResponseEntity<String>("", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
