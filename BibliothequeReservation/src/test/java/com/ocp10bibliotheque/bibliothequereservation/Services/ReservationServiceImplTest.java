package com.ocp10bibliotheque.bibliothequereservation.Services;

import com.ocp10bibliotheque.bibliothequereservation.DAO.LendingRepository;
import com.ocp10bibliotheque.bibliothequereservation.Entites.Book;
import com.ocp10bibliotheque.bibliothequereservation.Entites.Lending;
import com.ocp10bibliotheque.bibliothequereservation.Entites.UserAccount;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationServiceImplTest {
    @Mock
    LendingRepository lendingRepository;

    @InjectMocks
    private ReservationServiceImpl manager = new ReservationServiceImpl(lendingRepository);



    @Test
    public void testCheckReservationConditionByUserLoanNOK() throws Exception {
        // Création d'un utilisateur et d'un livre fictifs pour le test
        UserAccount user = new UserAccount();
        user.setMail("test@test.com");

        Book book = new Book();
        book.setId(1);

        // Création d'un emprunt fictif en cours pour le livre et l'utilisateur
        Lending lending = new Lending();
        lending.setStatus("En cours");
        lending.setBook(book);
        lending.setUserAccount(user);

        List<Lending> lendingList = new ArrayList<>();
        lendingList.add(lending);

        // Simulation de la méthode "findByBookIdAndUserAccountMail" du repository
        when(lendingRepository.findByBookIdAndUserAccountMail(book.getId(), user.getMail())).thenReturn(lendingList);

        // Appel de la méthode à tester
        Assertions.assertThrows(Exception.class, () -> manager.checkReservationConditionByUserLoan(user, book));

        // Vérification que l'exception a été lancée
        verify(lendingRepository).findByBookIdAndUserAccountMail(book.getId(), user.getMail());

    }

    @Test
    public void testCheckReservationConditionByUserLoanOK() throws Exception {
        // Création d'un utilisateur et d'un livre fictifs pour le test
        UserAccount user = new UserAccount();
        user.setMail("test@test.com");

        Book book = new Book();
        book.setId(1);

        // Création d'un emprunt fictif en cours pour le livre et l'utilisateur
        Lending lending = new Lending();
        lending.setStatus("Terminé");
        lending.setBook(book);
        lending.setUserAccount(user);

        List<Lending> lendingList = new ArrayList<>();
        lendingList.add(lending);

        // Simulation de la méthode "findByBookIdAndUserAccountMail" du repository
        when(lendingRepository.findByBookIdAndUserAccountMail(book.getId(), user.getMail())).thenReturn(lendingList);

        // Appel de la méthode à tester
        assertTrue(true);
        manager.checkReservationConditionByUserLoan(user, book);

    }
}
