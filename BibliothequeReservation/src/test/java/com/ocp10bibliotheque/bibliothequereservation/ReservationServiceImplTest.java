package com.ocp10bibliotheque.bibliothequereservation;

import com.ocp10bibliotheque.bibliothequereservation.Entites.Book;
import com.ocp10bibliotheque.bibliothequereservation.Entites.UserAccount;
import com.ocp10bibliotheque.bibliothequereservation.Services.ReservationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {

    @BeforeEach
    public void setup() {
        //lenient().when(dao.getComptabiliteDao()).thenReturn(comptabiliteDao);
    }

    ReservationServiceImpl manager = new ReservationServiceImpl();

    // ==================== TESTS UNITAIRES ====================
    @Test
    public void  checkReservationConditionByUserLoanNOK() throws Exception{
       // "Cet utilisateur ne peut pas faire une réservation car un prêt est en cours pour cet ouvrage !"
        assertThrows(Exception .class, ()-> {
            Book book = new Book();
            book.setId(1);
            UserAccount userAccount = new UserAccount("arnaud.biasin@orange.fr","Test1234*");
            manager.checkReservationConditionByUserLoan(userAccount,book);
        });
    }
    @Test
    public void  checkReservationConditionByUserLoanOK() throws Exception{

        Assertions.assertTrue(true);
        Book book = new Book();
        book.setId(1);
        UserAccount userAccount = new UserAccount("florian.marsaletta@orange.fr","Test1234*");
       manager.checkReservationConditionByUserLoan(userAccount,book);
    }
    // ==================== TESTS INTEGRATION ====================

}
