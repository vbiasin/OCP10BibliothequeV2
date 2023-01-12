package com.ocp7bibliotheque.bibliothequeweb.Controllers;

import com.ocp7bibliotheque.bibliothequeweb.Proxies.BibliothequeReservationProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebReservationController {

    @Autowired
    private BibliothequeReservationProxy reservationProxy;
    @GetMapping("/reservations")
    public String reservation() {
        UserDetails activeUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(reservationProxy.getReservationPage(activeUser.getUsername())==false) return "redirect:/contact";
        return "reservation";
    }
}
