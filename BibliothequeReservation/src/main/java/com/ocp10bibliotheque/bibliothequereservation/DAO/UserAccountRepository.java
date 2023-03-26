package com.ocp10bibliotheque.bibliothequereservation.DAO;


import com.ocp10bibliotheque.bibliothequereservation.Entites.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount,Integer> {

    Optional<UserAccount> findByMail(String mail);

}
