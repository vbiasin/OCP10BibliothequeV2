package com.ocp7bibliotheque.bibliothequeadministration.Services;

import com.ocp7bibliotheque.bibliothequeadministration.DAO.ContactRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.LendingRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.RoleRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.UserAccountRepository;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.Contact;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.Lending;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.Role;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    LendingRepository lendingRepository;

    @Override
    public UserAccount register(UserAccount account) throws Exception {
        Optional<Role> defaultRole = roleRepository.findByName("USER");
        if(defaultRole.isEmpty()) throw new Exception("Erreur lors de l'affectation du Role USER");
        Optional<Role> employeeRole = roleRepository.findByName("EMPLOYEE");
        if(employeeRole.isEmpty()) throw new Exception("Erreur lors de l'affectation du Role EMPLOYEE");
        Optional<Role> adminRole = roleRepository.findByName("ADMIN");
        if(adminRole.isEmpty()) throw new Exception("Erreur lors de l'affectation du Role ADMIN");
        Optional<UserAccount> newUser = userAccountRepository.findByMail(account.getMail());
        if(!newUser.isEmpty()) throw new Exception("Un utilisateur avec cette adresse mail existe déjà !");
        Collection<Role> roles = new ArrayList<Role>();
        roles.add(defaultRole.get());
        roles.add(employeeRole.get());
        roles.add(adminRole.get());
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
        List<Lending> listLoans = lendingRepository.findByUserAccount(account.get());
        for (Lending loan:listLoans
        ) {
            lendingRepository.delete(loan);
        }
        userAccountRepository.delete(account.get());
    }

    @Override
    public List<Contact> getListWithNoDoublon(List<Contact> contacts) throws Exception {
        List<Contact> noDoublonContacts = new ArrayList<>();
        if(!contacts.isEmpty()){
            for( Contact contact: contacts ){
                if(noDoublonContacts.isEmpty()) noDoublonContacts.add(contact);
                boolean noDoublon = true;
                for (Contact noDoublonContact:noDoublonContacts) {
                    if (contact.getLastName().equals(noDoublonContact.getLastName()) && contact.getFirstName().equals(noDoublonContact.getFirstName())){noDoublon=false;
                    }
                }
                if (noDoublon==true) noDoublonContacts.add(contact);
            }
        }
        return noDoublonContacts;
    }

    @Override
    public List<UserAccount> searchUserAccount(String mail, String lastName, String firstName) throws Exception {

        List<Contact> noDoublonContacts = getListWithNoDoublon(contactRepository.findByLastNameOrFirstName(lastName, firstName));

        List<UserAccount> result = new ArrayList<>();
        List<UserAccount> resultWithContact = new ArrayList<>();

       if(!mail.equals("toto@exemple.com")){
           UserAccount userAccount = userAccountRepository.findByMail(mail).get();
           result.add(userAccount);
       }

       if(noDoublonContacts.isEmpty() && result.isEmpty()){
            result = userAccountRepository.findAll();
            for (UserAccount userAccount:result){
                if(userAccount.getContact()!=null) {
                    resultWithContact.add(userAccount);
                }
            }
            return resultWithContact;
       }
       else{

           for (Contact contact: noDoublonContacts) {
               UserAccount currentUserAccount = userAccountRepository.findByContact(contact).get();
               if (!currentUserAccount.getMail().equals(mail))
                   result.add(currentUserAccount);
           }
       }

        return result;
    }
}
