package com.ocp10bibliotheque.bibliothequereservation.Services;


import com.ocp10bibliotheque.bibliothequereservation.DAO.UserAccountRepository;
import com.ocp10bibliotheque.bibliothequereservation.Entites.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserAccountServiceImpl implements IUserAccountService {


    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public Boolean checkUserAccountContact(String mail) throws Exception {
        Optional<UserAccount> userAccount = userAccountRepository.findByMail(mail);
        if(userAccount.isEmpty()) throw new Exception ("Cet utilisateur n'existe pas !");
        if(userAccount.get().getContact()==null) return false;
        return true;
    }
}
