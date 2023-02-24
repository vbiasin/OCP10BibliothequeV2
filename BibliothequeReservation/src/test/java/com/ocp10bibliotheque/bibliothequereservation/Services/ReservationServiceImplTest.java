package com.ocp10bibliotheque.bibliothequereservation.Services;

import com.ocp10bibliotheque.bibliothequereservation.DAO.BookRepository;
import com.ocp10bibliotheque.bibliothequereservation.DAO.LendingRepository;
import com.ocp10bibliotheque.bibliothequereservation.DAO.ReservationRepository;
import com.ocp10bibliotheque.bibliothequereservation.DAO.UserAccountRepository;
import com.ocp10bibliotheque.bibliothequereservation.Entites.*;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {
    @Mock
    LendingRepository lendingRepository;

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ReservationServiceImpl manager = new ReservationServiceImpl(lendingRepository,reservationRepository, emailSender,userAccountRepository,bookRepository);

    private Book book = new Book();
    private UserAccount user = new UserAccount();

    @Test
    public void testCheckReservationConditionByUserLoanNOK() throws Exception {
        // Création d'un utilisateur et d'un livre fictifs pour le test
        user.setMail("test@test.com");
        book.setId(1);

        // Création d'un emprunt fictif en cours pour le livre et l'utilisateur
        Lending lending = new Lending("En cours",user,book);

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
        user.setMail("test@test.com");
        book.setId(1);

        // Création d'un emprunt fictif terminé pour le livre et l'utilisateur
        Lending lending = new Lending("Terminé",user,book);

        List<Lending> lendingList = new ArrayList<>();
        lendingList.add(lending);

        // Simulation de la méthode "findByBookAndUserAccount" du repository
        when(lendingRepository.findByBookIdAndUserAccountMail(book.getId(), user.getMail())).thenReturn(lendingList);

        // Appel de la méthode à tester
        Assertions.assertTrue(true);
        manager.checkReservationConditionByUserLoan(user, book);
    }

    @Test
    public void testCheckReservationConditionByUserReservationNOK_EnCours() throws Exception {
        // Création d'un utilisateur et d'un livre fictifs pour le test
        UserAccount user = new UserAccount();
        user.setMail("test@test.com");

        // Création d'une réservation fictive en cours pour le livre et l'utilisateur
        Reservation reservation = new Reservation("en cours",user,book);

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation);

        // Simulation de la méthode "findByBookAndUserAccount" du repository
        when(reservationRepository.findByBookAndUserAccount(book, user)).thenReturn(reservationList);

        // Appel de la méthode à tester
        Assertions.assertThrows(Exception.class, () -> manager.checkReservationConditionByUserReservation(user, book));

        // Vérification que l'exception a été lancée
        verify(reservationRepository).findByBookAndUserAccount(book, user);
    }

    @Test
    public void testCheckReservationConditionByUserReservationNOK_EnAttente() throws Exception {
        // Création d'un utilisateur et d'un livre fictifs pour le test
        user.setMail("test@test.com");

        // Création d'une réservation fictive en attente pour le livre et l'utilisateur
        Reservation reservation = new Reservation("en attente",user,book);

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation);

        // Simulation de la méthode "findByBookAndUserAccount" du repository
        when(reservationRepository.findByBookAndUserAccount(book, user)).thenReturn(reservationList);

        // Appel de la méthode à tester
        Assertions.assertThrows(Exception.class, () -> manager.checkReservationConditionByUserReservation(user, book));

        // Vérification que l'exception a été lancée
        verify(reservationRepository).findByBookAndUserAccount(book, user);
    }

    @Test
    public void testCheckReservationConditionByUserReservationOK() throws Exception {
        // Création d'un utilisateur et d'un livre fictifs pour le test
        user.setMail("test@test.com");

        // Création d'une réservation fictive valide (Terminée ou Annulée) pour le livre et l'utilisateur
        Reservation reservation = new Reservation("Annulée",user,book);

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation);

        // Simulation de la méthode "findByBookAndUserAccount" du repository
        when(reservationRepository.findByBookAndUserAccount(book, user)).thenReturn(reservationList);

        // Appel de la méthode à tester
        Assertions.assertTrue(true);
        manager.checkReservationConditionByUserReservation(user, book);
    }

    @Test
    public void testCheckFirstReservation_shouldReturnTrue() throws Exception {
        // Arrange
        UserAccount user1 = new UserAccount();
        user1.setMail("test1@test.com");
        UserAccount user2 = new UserAccount();
        user2.setMail("test2@test.com");
        Reservation reservation1 = new Reservation("Terminée",user1,book);
        Reservation reservation2 = new Reservation("Terminée",user2,book);
        List<Reservation> listReservation = new ArrayList<>();
        listReservation.add(reservation1);
        listReservation.add(reservation2);

        when(reservationRepository.findByBook(book)).thenReturn(listReservation);

        // Act
        boolean result = manager.checkFirstReservation(reservation1);

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void testCheckFirstReservation_shouldReturnFalse() throws Exception {
        // Arrange
        UserAccount user1 = new UserAccount();
        user1.setMail("test1@test.com");
        UserAccount user2 = new UserAccount();
        user2.setMail("test2@test.com");
        Reservation reservation1 = new Reservation("Terminée",user1,book);
        Reservation reservation2 = new Reservation("en attente",user2,book);
        List<Reservation> listReservation = new ArrayList<>();
        listReservation.add(reservation1);
        listReservation.add(reservation2);

        when(reservationRepository.findByBook(book)).thenReturn(listReservation);

        // Act
        boolean result = manager.checkFirstReservation(reservation1);

        // Assert
        Assertions.assertFalse(result);
    }

    @Test
    public void testCheckFirstReservationWithNoMatchingReservations() throws Exception {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setBook(book);
        when(reservationRepository.findByBook(book)).thenReturn(new ArrayList<>());

        // Act
        boolean result = manager.checkFirstReservation(reservation);

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void testCheckFirstReservationWithMatchingReservations() throws Exception {
        // Arrange
        Reservation reservation1 = new Reservation();
        reservation1.setBook(book);
        reservation1.setStatus("Terminée");
        Reservation reservation2 = new Reservation();
        reservation2.setBook(book);
        reservation2.setStatus("En cours");
        List<Reservation> listReservation = new ArrayList<>();
        listReservation.add(reservation1);
        listReservation.add(reservation2);
        when(reservationRepository.findByBook(book)).thenReturn(listReservation);

        // Act
        boolean result = manager.checkFirstReservation(reservation1);

        // Assert
        Assertions.assertFalse(result);
    }

    @Test
    public void testStartReservation() throws Exception {
        // Arrange
        Reservation reservation = new Reservation();
        UserAccount userAccount = new UserAccount();
        userAccount.setMail("test@test.com");
        reservation.setUserAccount(userAccount);
        Library library = new Library();
        library.setName("Test Library");
        reservation.setLibrary(library);
        book.setTitle("Test Book");
        reservation.setBook(book);

        // Act
        manager.startReservation(reservation);

        // Assert
        Assertions.assertEquals("en cours", reservation.getStatus());
        Assertions.assertNotNull(reservation.getStartDate());
        Assertions.assertNotNull(reservation.getEndDate());
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
        Assertions.assertTrue(reservation.isMailIsSend());
    }

    @Test
    public void testReserveWithValidParameters() throws Exception {
        // Arrange
        book.setId(1);
        book.setNumberExemplarTotal(10);
        book.setCurrentNumberReservation(2);
        user.setMail("test@test.com");
        Reservation reservation = new Reservation();
        reservation.setUserAccount(user);
        reservation.setBook(book);
        Library library = new Library();
        library.setName("Test Library");
        reservation.setLibrary(library);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        ReservationServiceImpl reservationServiceSpy = Mockito.spy(manager);
        doNothing().when(reservationServiceSpy).checkReservationConditionByUser(user, book);
        doNothing().when(reservationServiceSpy).startReservation(reservation);

        when(userAccountRepository.findByMail("test@test.com")).thenReturn(Optional.of(user));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);


        // Act
        Reservation result = manager.reserve("test@test.com", 1);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(reservation, result);
        Assertions.assertEquals("en attente", result.getStatus());
        Assertions.assertEquals(3, book.getCurrentNumberReservation());
    }

    @Test
    public void testReserveWithInvalidBookId() {
        // Arrange
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(Exception.class, () -> manager.reserve("test@test.com", 1));
    }

    @Test
    public void testReserveWithInvalidUserAccountMail() {
        // Arrange
        book.setId(1);
        book.setNumberExemplarTotal(10);
        book.setCurrentNumberReservation(2);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(userAccountRepository.findByMail("test@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(Exception.class, () -> manager.reserve("test@test.com", 1));
    }

    @Test
    public void testReserveWithMaxReservationsReached() {
        // Arrange
        book.setId(1);
        book.setNumberExemplarTotal(10);
        book.setCurrentNumberReservation(20);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        // Act & Assert
        Assertions.assertThrows(Exception.class, () -> manager.reserve("test@test.com", 1));
    }
}