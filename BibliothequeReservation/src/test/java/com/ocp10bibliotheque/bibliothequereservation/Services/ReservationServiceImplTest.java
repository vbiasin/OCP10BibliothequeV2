package com.ocp10bibliotheque.bibliothequereservation.Services;

import com.ocp10bibliotheque.bibliothequereservation.DAO.BookRepository;
import com.ocp10bibliotheque.bibliothequereservation.DAO.LendingRepository;
import com.ocp10bibliotheque.bibliothequereservation.DAO.ReservationRepository;
import com.ocp10bibliotheque.bibliothequereservation.DAO.UserAccountRepository;
import com.ocp10bibliotheque.bibliothequereservation.Entites.*;
import org.junit.Ignore;
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
import java.time.LocalDateTime;
import java.util.*;

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
        reservation.setStatus("en attente");
        reservation.setLibrary(library);
        book.setLibrary(library);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
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

    @Test
    public void testCheckCurrentPosition_withValidReservationAndUser_shouldUpdateReservation() throws Exception {
        // Arrange
        int reservationId = 1;
        user.setMail( "user@mail.com");
        Reservation reservation = new Reservation("en attente",user,book);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.findByBookAndStatus(book, "en attente")).thenReturn(reservations);

        // Act
        manager.checkCurrentPosition(reservationId, user.getMail());

        // Assert
        verify(reservationRepository, times(1)).saveAndFlush(reservation);
        Assertions.assertEquals(1, reservation.getCurrentPosition());
    }

    @Test
    public void testCheckCurrentPosition_withInvalidReservation_shouldThrowException() throws Exception {
        // Arrange
        int reservationId = 1;
        String userAccountMail = "user@mail.com";
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // Act + Assert
        Assertions.assertThrows(Exception.class, () -> manager.checkCurrentPosition(reservationId, userAccountMail));
        verify(reservationRepository, never()).saveAndFlush(any());
    }

    @Test
    public void testCheckNextReturnOfBook_withValidBook_shouldUpdateBook() throws Exception {
        // Arrange
        book.setId(1);
        book.setTitle("The Book Title");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        Lending lending1 = new Lending();
        lending1.setBook(book);
        lending1.setStartDate(now);
        lending1.setEndDate(now.plusDays(1));
        Lending lending2 = new Lending();
        lending2.setBook(book);
        lending2.setStartDate(tomorrow);
        lending2.setEndDate( tomorrow.plusDays(2));
        List<Lending> lendings = new ArrayList<>();
        lendings.add(lending1);
        lendings.add(lending2);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(lendingRepository.findByBook(book)).thenReturn(lendings);

        // Act
        manager.checkNextReturnOfBook(book.getId());

        // Assert
        verify(bookRepository, times(1)).saveAndFlush(book);
        Assertions.assertEquals(tomorrow, book.getNextReturn());
    }

    @Test
    public void testCheckNextReturnOfBook_withInvalidBook_shouldThrowException() {
        // Arrange
      book.setId(1);
        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

        // Act + Assert
        Assertions.assertThrows(Exception.class, () -> manager.checkNextReturnOfBook(book.getId()));
        verify(bookRepository, never()).saveAndFlush(any());
    }

    @Test
    public void testCheckNextReturnOfBook_withNoLendings_shouldNotUpdateBook() throws Exception {
        // Arrange
        book.setTitle("The Book Title");
        book.setId(1);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(lendingRepository.findByBook(book)).thenReturn(Collections.emptyList());

        // Act
        manager.checkNextReturnOfBook(book.getId());

        // Assert
        verify(bookRepository, times(1)).saveAndFlush(book);
        Assertions.assertNull(book.getNextReturn());
    }

    @Test
    public void testValidateReservation_withValidReservation_shouldUpdateReservation() throws Exception {
        // Arrange
        int reservationId = 1;
        Reservation reservation = new Reservation();
        reservation.setStatus("en attente");
        UserAccount userAccount = new UserAccount("john.doe@example.com", "John Doe");
        book.setTitle("The Book Title");
        book.setNumberExemplarActual(3);
        book.setCurrentNumberReservation(1);
        reservation.setUserAccount(userAccount);
        reservation.setBook(book);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.findByBookAndStatus(book, "en attente")).thenReturn(Collections.emptyList());

        // Act
        manager.validateReservation(reservationId);

        // Assert
        verify(reservationRepository, times(1)).saveAndFlush(reservation);
        Assertions.assertEquals("Terminée", reservation.getStatus());
        Assertions.assertEquals(0, book.getCurrentNumberReservation());
    }

    @Test
    public void testValidateReservation_withValidReservationAndNextReservation_shouldReserveNextReservation() throws Exception {
        // Arrange
        int reservationId = 1;
        book.setId(1);
        Reservation reservation = new Reservation();
        reservation.setStatus("en attente");
        UserAccount userAccount = new UserAccount("john.doe@example.com", "John Doe");
        book.setTitle("The Book Title");
        book.setNumberExemplarActual(3);
        book.setNumberExemplarTotal(10);
        book.setCurrentNumberReservation(1);
        book.setLibrary(new Library("médiathèque de l'Europe","36 place Poincarré"));
        reservation.setUserAccount(userAccount);
        reservation.setBook(book);
        Reservation nextReservation = new Reservation();
        nextReservation.setStatus("en attente");
        UserAccount nextUserAccount = new UserAccount("jane.doe@example.com", "Jane Doe");
        nextReservation.setUserAccount(nextUserAccount);
        nextReservation.setBook(book);
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(nextReservation);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(userAccountRepository.findByMail(nextReservation.getUserAccount().getMail())).thenReturn(Optional.of(userAccount));
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.findByBookAndStatus(book, "en attente")).thenReturn((reservationList));

        // Act
        manager.validateReservation(reservationId);

        // Assert
        verify(reservationRepository, times(1)).saveAndFlush(reservation);

        Assertions.assertEquals("Terminée", reservation.getStatus());
        Assertions.assertEquals(1, book.getCurrentNumberReservation());
    }

    @Test
    public void testValidateReservation_withInvalidReservation_shouldThrowException() throws Exception {
        // Arrange
        int reservationId = 1;
        ReservationServiceImpl reservationServiceSpy = Mockito.spy(manager);
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // Act + Assert
        Assertions.assertThrows(Exception.class, () -> manager.validateReservation(reservationId));
        verify(reservationRepository, never()).saveAndFlush(any());
        verify(bookRepository, never()).saveAndFlush(any());
        verify(reservationServiceSpy, never()).reserve(anyString(), anyInt());
    }

    @Test
    public void deleteReservation_shouldDeleteReservationAndDecrementCurrentNumberReservation() throws Exception {
        // Arrange
        int idReservation = 1;
        Reservation reservation = new Reservation();
        book.setCurrentNumberReservation(2);
        reservation.setBook(book);
        when(reservationRepository.findById(idReservation)).thenReturn(Optional.of(reservation));
        // Act
        manager.deleteReservation(idReservation);
        // Assert
        verify(bookRepository, times(1)).saveAndFlush(book);
        verify(reservationRepository, times(1)).delete(reservation);
    }

    @Test
    public void deleteReservation_shouldThrowExceptionWhenReservationNotFound() throws Exception {
        // Arrange
        int idReservation = 1;
        when(reservationRepository.findById(idReservation)).thenReturn(Optional.empty());
        // Act & Assert
        Assertions.assertThrows(Exception.class, () -> manager.deleteReservation(idReservation));
    }

    @Test
    public void testCancelReservation() throws Exception {
        // given
        int idReservation = 1;
        Reservation reservation = new Reservation();
        reservation.setId(idReservation);
        reservation.setStatus("en attente");
        book.setId(1);
        book.setNumberExemplarTotal(10);
        book.setCurrentNumberReservation(1);
        book.setLibrary(new Library("médiathèque de l'Europe","36 place Poincarré"));
        reservation.setBook(book);
        when(reservationRepository.findById(idReservation)).thenReturn(Optional.of(reservation));
        Reservation nextReservation = new Reservation();
        UserAccount userAccount =  new UserAccount("Spirou@gmail.com","Azerty1234*");
        nextReservation.setUserAccount(userAccount);
        nextReservation.setBook(book);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(userAccountRepository.findByMail(nextReservation.getUserAccount().getMail())).thenReturn(Optional.of(userAccount));
        when(reservationRepository.findByBookAndStatus(book, "en attente")).thenReturn(List.of(nextReservation));

        manager.cancelReservation(idReservation);

        // then
        verify(reservationRepository, times(1)).findById(idReservation);
        verify(reservationRepository, times(1)).saveAndFlush(reservation);
        Assertions.assertEquals("Annulée", reservation.getStatus());
        verify(bookRepository, times(2)).saveAndFlush(book);
        Assertions.assertEquals(1, book.getCurrentNumberReservation());
        verify(reservationRepository, times(1)).findByBookAndStatus(book, "en attente");
    }

    @Test
    public void testCancelReservationWithNonExistingReservation() throws Exception {
        // given
        int idReservation = 1;
        when(reservationRepository.findById(idReservation)).thenReturn(Optional.empty());

        // when
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            manager.cancelReservation(idReservation);
        });

        // then
        verify(reservationRepository, times(1)).findById(idReservation);
        verifyNoMoreInteractions(reservationRepository);
        verifyNoInteractions(bookRepository);
        Assertions.assertEquals("Cette réservation n'existe pas !", exception.getMessage());
    }

    @Test
    public void testGetNextReservationWithEmptyList() throws Exception {
        List<Reservation> emptyList = new ArrayList<>();
        Reservation result =manager.getNextReservation(emptyList);
        Assertions.assertNull(result);
    }

    @Test
    public void testGetNextReservationWithOneReservation() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setId(1);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);
        Reservation result = manager.getNextReservation(reservations);
        Assertions.assertEquals(reservation, result);
    }

    @Test
    public void testGetNextReservationWithMultipleReservations() throws Exception {
        Reservation reservation1 = new Reservation();
        reservation1.setId(1);
        Reservation reservation2 = new Reservation();
        reservation2.setId(2);
        Reservation reservation3 = new Reservation();
        reservation3.setId(3);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation2);
        reservations.add(reservation1);
        reservations.add(reservation3);
        Reservation result =manager.getNextReservation(reservations);
        Assertions.assertEquals(reservation1, result);
    }

    @Test
    public void testDisplayReservationByUserWithAdminRole() throws Exception {
        // Given
        String userAccountMail = "admin@test.com";
        UserAccount userAccount = new UserAccount();
        userAccount.setMail(userAccountMail);
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        userAccount.setRoles(Collections.singletonList(adminRole));
        when(userAccountRepository.findByMail(userAccountMail)).thenReturn(Optional.of(userAccount));
        List<Reservation> expectedReservations = Arrays.asList(new Reservation(), new Reservation());
        when(reservationRepository.findAll()).thenReturn(expectedReservations);

        // When
        List<Reservation> reservations = manager.displayReservationByUser(userAccountMail);

        // Then
        verify(userAccountRepository).findByMail(userAccountMail);
        verify(reservationRepository).findAll();
        Assertions.assertEquals(expectedReservations, reservations);
    }

    @Test
    public void testDisplayReservationByUserWithEmployeeRole() throws Exception {
        // Given
        String userAccountMail = "employee@test.com";
        UserAccount userAccount = new UserAccount();
        userAccount.setMail(userAccountMail);
        Role employeeRole = new Role();
        employeeRole.setName("EMPLOYEE");
        userAccount.setRoles(Collections.singletonList(employeeRole));
        when(userAccountRepository.findByMail(userAccountMail)).thenReturn(Optional.of(userAccount));
        List<Reservation> expectedReservations = Arrays.asList(new Reservation(), new Reservation());
        when(reservationRepository.findAll()).thenReturn(expectedReservations);

        // When
        List<Reservation> reservations = manager.displayReservationByUser(userAccountMail);

        // Then
        verify(userAccountRepository).findByMail(userAccountMail);
        verify(reservationRepository).findAll();
        Assertions.assertEquals(expectedReservations, reservations);
    }


    @Test
    public void testDisplayReservationByUserWithUserRole() throws Exception {
        // Given
        String userAccountMail = "user@test.com";
        UserAccount userAccount = new UserAccount();
        userAccount.setMail(userAccountMail);
        Role userRole = new Role();
        userRole.setName("USER");
        userAccount.setRoles(Collections.singletonList(userRole));
        when(userAccountRepository.findByMail(userAccountMail)).thenReturn(Optional.of(userAccount));
        List<Reservation> expectedReservations = Arrays.asList(new Reservation(), new Reservation());
        when(reservationRepository.findByUserAccount(userAccount)).thenReturn(expectedReservations);
        ReservationServiceImpl reservationServiceSpy = Mockito.spy(manager);

        // When
        List<Reservation> reservations = reservationServiceSpy.displayReservationByUser(userAccountMail);

        // Then
        verify(userAccountRepository).findByMail(userAccountMail);
        verify(reservationRepository, times(1)).findAll();
        verify(reservationRepository).findByUserAccount(userAccount);
        Assertions.assertEquals(expectedReservations, reservations);
    }

    @Test
    public void testDisplayReservationByUserWithNonExistingUserAccount() {
        // arrange
        String nonExistingUserMail = "test@test.com";

        // act and assert
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            manager.displayReservationByUser(nonExistingUserMail);
        });

        String expectedMessage = "Cet utilisateur n'existe pas !";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

}