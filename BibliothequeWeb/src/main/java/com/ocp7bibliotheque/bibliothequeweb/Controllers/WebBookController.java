package com.ocp7bibliotheque.bibliothequeweb.Controllers;
import com.ocp7bibliotheque.bibliothequeweb.DTO.BookDTO;
import com.ocp7bibliotheque.bibliothequeweb.Entites.Book;
import com.ocp7bibliotheque.bibliothequeweb.Proxies.BibliothequeBookProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
public class WebBookController {

    @Autowired
    private BibliothequeBookProxy bookProxy;

    @GetMapping("/books")
    public String book() {
        UserDetails activeUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(bookProxy.getBookPage(activeUser.getUsername())==false) return "redirect:/contact";
        return "book";
    }

    @PostMapping("/addBook")
    public String book(String resume, String title, String author, int numberExemplarTotal, int idLibrary) {
        Book book = new Book(new Date(),resume,title,author,numberExemplarTotal);
        BookDTO bookDTO = new BookDTO(idLibrary, book);
        bookProxy.addBook(bookDTO);
        return  "book";
    }

    @PostMapping("/modifyBook")
    public String book(int idBook, int numberExemplarTotal) {
        BookDTO bookDTO = new BookDTO(idBook, numberExemplarTotal);
        bookProxy.modifyBook(bookDTO);
        return  "book";
    }

    @PostMapping("/removeBook")
    public String removeRole(@RequestParam int idBook) {
        bookProxy.removeBook(idBook);
        return  "book";
    }

    @PostMapping("/searchBook")
    public String searchLibrary(@RequestParam(name="title", defaultValue="titreLivre") String title, @RequestParam(name="author", defaultValue="auteurLivre") String author,
                                Model model) throws Exception {


        BookDTO bookDTO = new BookDTO(author,title);
       List<Book> pageListBooks;
        try {

            pageListBooks = bookProxy.searchBook(bookDTO);
            model.addAttribute("pageListBooks",pageListBooks);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "book";
    }

}
