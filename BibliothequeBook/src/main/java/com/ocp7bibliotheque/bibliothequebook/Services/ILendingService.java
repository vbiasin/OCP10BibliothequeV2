package com.ocp7bibliotheque.bibliothequebook.Services;

import com.ocp7bibliotheque.bibliothequebook.Entites.Lending;
import java.util.List;

public interface ILendingService {

    public Lending borrow(String userAccountMail, int idBook) throws Exception;
    public Lending extendLoan(int idLending) throws Exception;
    public List<Lending> displayLoan(String activeUserAccountMail) throws Exception;
    public void returnLoan(int idLending) throws Exception;
    public void checkIsExtensible(Lending lending) throws Exception;

}
