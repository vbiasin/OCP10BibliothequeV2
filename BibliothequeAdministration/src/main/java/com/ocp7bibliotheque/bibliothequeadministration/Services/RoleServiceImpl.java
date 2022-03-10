package com.ocp7bibliotheque.bibliothequeadministration.Services;

import com.ocp7bibliotheque.bibliothequeadministration.DAO.RoleRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.UserAccountRepository;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.Role;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
public class RoleServiceImpl implements IRoleService{

    @Autowired
    UserAccountRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void addRoleToUserAccount(String mail, int idRole) throws Exception {
        Optional<UserAccount> userAccount = userRepository.findByMail(mail);
        if(userAccount.isEmpty()) throw new Exception("L'utilisateur spécifié n'existe pas !");
        Optional<Role> role = roleRepository.findById(idRole);
        if(role.isEmpty()) throw new Exception("Aucun rôle ne correspond à cet identifiant !");
        ArrayList<Role> roles = (ArrayList<Role>) userAccount.get().getRoles();
        roles.add(role.get());
        userAccount.get().setRoles(roles);
        userRepository.saveAndFlush(userAccount.get());
    }

    @Override
    public void removeRoleFromUserAccount(String mail, int idRole) throws Exception {
        Optional<UserAccount> userAccount = userRepository.findByMail(mail);
        if(userAccount.isEmpty()) throw new Exception("L'utilisateur spécifié n'existe pas !");
        Optional<Role> role = roleRepository.findById(idRole);
        if(role.isEmpty()) throw new Exception("Aucun rôle ne correspond à cet identifiant !");
        ArrayList<Role> roles = (ArrayList<Role>) userAccount.get().getRoles();
        roles.remove(role.get());
        userAccount.get().setRoles(roles);
        userRepository.saveAndFlush(userAccount.get());
    }
}
