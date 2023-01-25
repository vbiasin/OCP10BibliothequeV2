package com.ocp7bibliotheque.bibliothequeweb.Proxies;


import com.ocp7bibliotheque.bibliothequeweb.DTO.ReservationDTO;
import com.ocp7bibliotheque.bibliothequeweb.Entites.Reservation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "BibliothequeReservation", url = "localhost:8480")
public interface BibliothequeReservationProxy {
    @GetMapping(value="/reservationsBack")
    Boolean getReservationPage(@RequestParam("activeUsername")  String activeUsername);

    @PostMapping(value = "/reserveBack")
    Reservation reserve(@RequestBody ReservationDTO reservationlDTO);

    @GetMapping(value="/displayReservationsBack")
    List<Reservation> displayReservations(@RequestParam("activeUsername") String activeUsername);

}
