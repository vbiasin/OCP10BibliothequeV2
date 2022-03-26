package com.ocp7bibliotheque.bibliothequeadministration.Services;

import com.ocp7bibliotheque.bibliothequeadministration.DAO.ContactRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.RoleRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.UserAccountRepository;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.Contact;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.Role;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserAccountServiceImpl implements IUserAccountService{

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ContactRepository contactRepository;

    @Override
    public UserAccount register(UserAccount account) throws Exception {
        Optional<Role> defaultRole = roleRepository.findByName("USER");
        if(defaultRole.isEmpty()) throw new Exception("Erreur lors de l'affectation du Role USER");
        Optional<Role> employeeRole = roleRepository.findByName("EMPLOYEE");
        if(employeeRole.isEmpty()) throw new Exception("Erreur lors de l'affectation du Role EMPLOYEE");
        /*Optional<Role> adminRole = roleRepository.findByName("ADMIN");
        if(adminRole.isEmpty()) throw new Exception("Erreur lors de l'affectation du Role ADMIN");*/
        Optional<UserAccount> newUser = userAccountRepository.findByMail(account.getMail());
        if(!newUser.isEmpty()) throw new Exception("Un utilisateur avec cette adresse mail existe déjà !");
        Collection<Role> roles = new ArrayList<Role>();
        roles.add(defaultRole.get());
        roles.add(employeeRole.get());
        //roles.add(adminRole.get());
        account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        account.setRoles(roles);
        return userAccountRepository.save(account);
    }

    @Override
    public boolean isValid(UserAccount account) throws Exception {
        String password= bCryptPasswordEncoder.encode(account.getPassword());
        System.out.println("mot de passe : " +account.getPassword());
        System.out.println("mot de passe crypté : " +password);

        Optional<UserAccount> newUserAccount = userAccountRepository.findByMail(account.getMail());
        String passwordDB=newUserAccount.get().getPassword();
        System.out.println("mot de passe DB : " +passwordDB);
        if(newUserAccount.isEmpty()) {
            throw new Exception("Utilisateur inexistant!");
        }
        if (!password.equals(passwordDB)){
            throw new Exception("Login ou mot de passe incorrect !");
        }
        return newUserAccount.get().getActive();
    }


    @Override
    public UserAccount getUserAccount(String mail) throws Exception {
        Optional<UserAccount>connectedUser = userAccountRepository.findByMail(mail);
        if(connectedUser.isEmpty()) throw new Exception("Cette adresse mail n'a pas été trouvée !");
        return connectedUser.get();
    }

    @Override
    public void removeUserAccount(int idUserAccount) throws Exception {
        Optional<UserAccount> account = userAccountRepository.findById(idUserAccount);
        if(account.isEmpty()) throw new Exception("Le compte utilisateur n'existe pas !");
        userAccountRepository.delete(account.get());
    }

    @Override
    public Page<UserAccount> searchUserAccount(String mail, String lastName, String firstName, int pages, int size) throws Exception {
       List<Contact> contacts = contactRepository.findByLastNameOrFirstName(lastName, firstName);
       List<Contact> noDoublonContacts = new ArrayList<>();
       for( Contact contact: contacts ){
          if(noDoublonContacts.isEmpty()) noDoublonContacts.add(contact);
           for (Contact noDoublonContact:noDoublonContacts) {
               if (!contact.getFirstName().equals(noDoublonContact.getFirstName()) || !contact.getLastName().equals(noDoublonContact.getLastName())){
                   noDoublonContacts.add(contact);
               }
           }
        }
       UserAccount userAccount = userAccountRepository.findByMail(mail).get();
       List<UserAccount> result = new ArrayList<>();
       result.add(userAccount);
        for (Contact contact: noDoublonContacts) {
            UserAccount currentUserAccount = userAccountRepository.findByContact(contact).get();
            if (!currentUserAccount.getMail().equals(mail))
            result.add(currentUserAccount);
        }

        for (UserAccount userAccountCheck:result) {
            System.out.println(userAccountCheck.getMail());
        }

       Page<UserAccount> resultPage = (Page<UserAccount>) result;
        return resultPage;
    }
}
