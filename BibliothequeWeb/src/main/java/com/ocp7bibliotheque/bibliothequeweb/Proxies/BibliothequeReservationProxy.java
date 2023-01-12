package com.ocp7bibliotheque.bibliothequeweb.Proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "BibliothequeReservation", url = "localhost:8480")
public interface BibliothequeReservationProxy {
    @GetMapping(value="/reservationsBack")
    Boolean getReservationPage(@RequestParam("activeUsername")  String activeUsername);
}
