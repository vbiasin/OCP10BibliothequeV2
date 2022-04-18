package com.ocp7bibliotheque.bibliothequeweb.Controllers;

import com.ocp7bibliotheque.bibliothequeweb.DTO.LibraryDTO;
import com.ocp7bibliotheque.bibliothequeweb.DTO.RoleDTO;
import com.ocp7bibliotheque.bibliothequeweb.DTO.UserAccountDTO;
import com.ocp7bibliotheque.bibliothequeweb.Entites.Library;
import com.ocp7bibliotheque.bibliothequeweb.Entites.UserAccount;
import com.ocp7bibliotheque.bibliothequeweb.Proxies.BibliothequeAdministrationUserProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebAdministrationController {

    @Autowired
    private BibliothequeAdministrationUserProxy userProxy;

    @GetMapping("/administration")
    public String administration() {
        return "administration";
    }

    @PostMapping("/addLibrary")
    public String library(String name, String address) {
        Library library = new Library(name, address);
        userProxy.addLibrary(library);
        return  "administration";
    }

    @PostMapping("/addRole")
    public String addRole(@RequestParam String userAccountMail,@RequestParam int idRole) {

        RoleDTO roleDTO = new RoleDTO(userAccountMail,idRole);
        userProxy.addRole(roleDTO);
        return  "administration";
    }

    @PostMapping("/searchUserAccount")
    public String searchUserAccount( @RequestParam(name="mail", defaultValue="toto@exemple.com") String mail,@RequestParam(name="lastName", defaultValue="azerty") String lastName,
                                    @RequestParam(name="firstName", defaultValue="qwerty") String firstName, @RequestParam(name="pageList", defaultValue="0") int pageList,@RequestParam(name="size",defaultValue="10") int size,Model model) throws Exception {

        System.out.println("mail: "+mail+" Nom: "+lastName+" Prénom: "+firstName+" PageList: "+ pageList+" Size: "+size);
        UserAccountDTO userAccountDTO = new UserAccountDTO(mail,lastName,firstName,pageList,size);
        Page<UserAccount> pageListUsers;
        try {

            pageListUsers = userProxy.searchUserAccount(userAccountDTO);
            model.addAttribute("listUsers",pageListUsers.getContent());
            int []pagesListUsers = new int[pageListUsers.getTotalPages()];
            model.addAttribute("pageListUsers",pageListUsers);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "administration";
    }

    @PostMapping("/removeRole")
    public String removeRole(@RequestParam String userAccountMail,@RequestParam int idRole) {
        RoleDTO roleDTO = new RoleDTO(userAccountMail,idRole);
        userProxy.removeRole(roleDTO);
        return  "administration";
    }

    @PostMapping("/searchLibrary")
    public String searchLibrary( @RequestParam(name="name", defaultValue="nomBibliotheque") String name,@RequestParam(name="address", defaultValue="adresseBibliotheque") String address,
                                   @RequestParam(name="pageList", defaultValue="0") int pageList,@RequestParam(name="size",defaultValue="10") int size,Model model) throws Exception {


       LibraryDTO libraryDTO = new LibraryDTO(name,address,pageList,size);
        Page<Library> pageListLibraries;
        try {

            pageListLibraries = userProxy.searchLibrary(libraryDTO);
            model.addAttribute("listLibrairies",pageListLibraries.getContent());
            int []pagesListLibraries = new int[pageListLibraries.getTotalPages()];
            model.addAttribute("pageListLibraries",pageListLibraries);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "administration";
    }

    @PostMapping("/removeUserAccount")
    public String removeUserAccount(@RequestParam int idUserAccount) {
        userProxy.removeUserAccount(idUserAccount);
        return "redirect:administration";
    }

    @PostMapping("/removeLibrary")
    public String removeLibrary(@RequestParam int idLibrary) {
        userProxy.removeLibrary(idLibrary);
        return "redirect:administration";
    }

}
