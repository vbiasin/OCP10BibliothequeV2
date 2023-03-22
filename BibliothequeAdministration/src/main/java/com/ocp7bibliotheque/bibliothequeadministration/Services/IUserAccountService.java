package com.ocp7bibliotheque.bibliothequeadministration.Services;

import com.ocp7bibliotheque.bibliothequeadministration.Entites.Contact;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.UserAccount;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUserAccountService {

    public UserAccount register(UserAccount account) throws Exception;
    public boolean isValid(UserAccount account) throws Exception;
    public UserAccount getUserAccount(String mail) throws Exception;
    public void removeUserAccount(int idUserAccount) throws Exception;
    public List<Contact> getListWithNoDoublon(List<Contact> contactsList) throws Exception;

    public List<UserAccount> searchUserAccount(String mail, String lastName, String firstName) throws Exception;

}
