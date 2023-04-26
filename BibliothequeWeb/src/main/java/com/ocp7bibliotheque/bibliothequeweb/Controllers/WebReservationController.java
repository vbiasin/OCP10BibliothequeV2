package com.ocp7bibliotheque.bibliothequeweb.Controllers;

import com.ocp7bibliotheque.bibliothequeweb.DTO.ReservationDTO;
import com.ocp7bibliotheque.bibliothequeweb.Entites.Lending;
import com.ocp7bibliotheque.bibliothequeweb.Entites.Reservation;
import com.ocp7bibliotheque.bibliothequeweb.Proxies.BibliothequeReservationProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebReservationController {

    @Autowired
    private BibliothequeReservationProxy reservationProxy;
    @GetMapping("/reservations")
    public String reservation( Model model) {
        UserDetails activeUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(reservationProxy.getReservationPage(activeUser.getUsername())==false) return "redirect:/contact";
        try {
            List<Reservation> listReservations = reservationProxy.displayReservations(activeUser.getUsername());
            model.addAttribute("listReservations",listReservations);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "reservation";
    }

    @PostMapping("/reserve")
    public String reserve(@RequestParam int idBook) {
        UserDetails activeUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String mail = activeUser.getUsername();
        ReservationDTO reservationDTO = new ReservationDTO(mail, idBook);
        reservationProxy.reserve(reservationDTO);
        return  "book";
    }

    @PostMapping("/cancelReservation")
    public String removeUserAccount(@RequestParam int idReservation) {
        reservationProxy.cancelReservation(idReservation);
        return "/reservation";
    }
}
