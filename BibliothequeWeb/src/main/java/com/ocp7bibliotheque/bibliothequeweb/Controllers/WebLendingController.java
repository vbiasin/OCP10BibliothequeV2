package com.ocp7bibliotheque.bibliothequeweb.Controllers;

import com.ocp7bibliotheque.bibliothequeweb.DTO.LendingDTO;
import com.ocp7bibliotheque.bibliothequeweb.Entites.Lending;
import com.ocp7bibliotheque.bibliothequeweb.Proxies.BibliothequeBookProxy;
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
public class WebLendingController {

    @Autowired
    private BibliothequeBookProxy bookProxy;

    @GetMapping("/loans")
    public String loan( Model model) {
        UserDetails activeUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(bookProxy.getLendingPage(activeUser.getUsername())==false) return "redirect:/contact";
        try {
            List<Lending> listLoans = bookProxy.displayLoans(activeUser.getUsername());
            model.addAttribute("listLoans",listLoans);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "loan";
    }

    @PostMapping("/borrow")
    public String borrow(@RequestParam int idBook) {
        UserDetails activeUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String mail = activeUser.getUsername();
        LendingDTO lendingDTO = new LendingDTO(mail, idBook);

        bookProxy.borrow(lendingDTO);
        return  "book";
    }

    @PostMapping("/extendLoan")
    public String extendLoan(@RequestParam int idLending) {
        bookProxy.extendLoan(idLending);
        return  "redirect:/loans";
    }



}
