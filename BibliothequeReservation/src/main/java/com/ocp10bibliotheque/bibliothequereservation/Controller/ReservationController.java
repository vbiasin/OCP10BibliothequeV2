package com.ocp10bibliotheque.bibliothequereservation.Controller;

import com.ocp10bibliotheque.bibliothequereservation.Services.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

    @Autowired
    private IUserAccountService userAccountService;

    @GetMapping("/reservationsBack")
    public ResponseEntity<Boolean> checkActiveUserContact(@RequestParam("activeUsername")  String activeUserName) throws Exception {
        try {
            return new ResponseEntity<Boolean>(userAccountService.checkUserAccountContact(activeUserName), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
