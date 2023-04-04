package com.ocp7bibliotheque.bibliothequebook;

import com.ocp7bibliotheque.bibliothequebook.DAO.BookRepository;
import com.ocp7bibliotheque.bibliothequebook.DAO.LendingRepository;
import com.ocp7bibliotheque.bibliothequebook.DAO.UserAccountRepository;
import com.ocp7bibliotheque.bibliothequebook.Entites.Book;
import com.ocp7bibliotheque.bibliothequebook.Entites.Lending;
import com.ocp7bibliotheque.bibliothequebook.Entites.Role;
import com.ocp7bibliotheque.bibliothequebook.Entites.UserAccount;
import com.ocp7bibliotheque.bibliothequebook.Services.LendingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class LendingServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private LendingRepository lendingRepository;

    @InjectMocks
    private LendingServiceImpl lendingService;

    private Book book;
    private UserAccount userAccount;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        book = new Book("Title", "Author");
        book.setNumberExemplarTotal(4);
        book.setAvailable(true);
        userAccount = new UserAccount("test@example.com", "toto1234");

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(userAccountRepository.findByMail("test@example.com")).thenReturn(Optional.of(userAccount));
    }

    @Test
    @DisplayName("Borrowing a book should succeed")
    void borrow_success() throws Exception {
        book.setNumberExemplarActual(3);
        Lending expectedLending = new Lending(userAccount, book);
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(book));
        when(userAccountRepository.findByMail(anyString())).thenReturn(Optional.of(userAccount));
        when(lendingRepository.save(any(Lending.class))).thenReturn(expectedLending);
        when(bookRepository.saveAndFlush(any(Book.class))).thenReturn(book);

        Lending actualLending = lendingService.borrow("test@example.com", 1);

        assertEquals(expectedLending, actualLending);
        assertEquals(book.getNumberExemplarActual(), actualLending.getBook().getNumberExemplarActual());
        assertTrue(actualLending.getBook().getAvailable());
    }

    @Test
    @DisplayName("Borrowing a non-existent book should throw an exception")
    void borrow_bookNotFound() {
        when(bookRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> lendingService.borrow("test@example.com", 1));

        assertEquals("Ce livre n'existe pas !", exception.getMessage());
    }

    @Test
    @DisplayName("Borrowing a book with an invalid user account should throw an exception")
    void borrow_userAccountNotFound() {
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(book));
        when(userAccountRepository.findByMail(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> lendingService.borrow("test@example.com", 1));

        assertEquals("Cet utilisateur n'existe pas !", exception.getMessage());
    }

    @Test
    @DisplayName("Borrowing a book with no available exemplar should throw an exception")
    void borrow_noExemplarAvailable() {
        book.setNumberExemplarActual(0);
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(book));
        //when(userAccountRepository.findByMail("test@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> lendingService.borrow("test@example.com", 1));

        assertEquals("Il n'y a plus d'exemplaire disponible pour ce livre !", exception.getMessage());
    }

    @Test
    @DisplayName("Extend an existing loan")
    void testExtendLoan() throws Exception {
        // Arrange
        int idLending = 1;
        LocalDateTime initialEndDate = LocalDateTime.now().plusDays(28);
        Lending lending = new Lending(userAccount,book);
        lending.setExtensible(true);
        when(lendingRepository.findById(idLending)).thenReturn(Optional.of(lending));
        when(lendingRepository.saveAndFlush(lending)).thenReturn(lending);

        // Act
        Lending extendedLending = lendingService.extendLoan(idLending);

        // Assert
        Assertions.assertFalse(extendedLending.isExtensible());
        assertEquals("Prolongé", extendedLending.getStatus());
        Assertions.assertEquals(initialEndDate.plusDays(28), extendedLending.getEndDate());
        verify(lendingRepository, times(1)).saveAndFlush(lending);
    }

    @Test
    @DisplayName("Try to extend a non-existing loan")
    void testExtendNonExistingLoan() throws Exception {
        // Arrange
        int idLending = 1;
        Optional<Lending> optionalLending = Optional.empty();
        when(lendingRepository.findById(idLending)).thenReturn(optionalLending);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            lendingService.extendLoan(idLending);
        });
        assertEquals("Ce prêt n'existe pas !'", exception.getMessage());
        verify(lendingRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Should return all loans when user has admin or employee role")
    void testDisplayLoanWithAdminOrEmployeeRole() throws Exception {
        UserAccount userAccount = new UserAccount();
        Role adminRole = new Role("ADMIN");
        List<Role> roles = new ArrayList<>(Arrays.asList(adminRole));
        userAccount.setRoles(roles);
        when(userAccountRepository.findByMail(anyString())).thenReturn(Optional.of(userAccount));
        when(lendingRepository.findAll()).thenReturn(new ArrayList<>());

        List<Lending> result = lendingService.displayLoan("test@test.com");

        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should return user's loans when user has no admin or employee role")
    void testDisplayLoanWithNoAdminOrEmployeeRole() throws Exception {
        UserAccount userAccount = new UserAccount();
        userAccount.setRoles(new ArrayList<>());
        when(userAccountRepository.findByMail(anyString())).thenReturn(Optional.of(userAccount));
        when(lendingRepository.findByUserAccount(any())).thenReturn(new ArrayList<>());

        List<Lending> result = lendingService.displayLoan("test@test.com");

        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should throw exception when user account is not found")
    void testDisplayLoanWithNonExistentUserAccount() throws Exception {
        when(userAccountRepository.findByMail(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(Exception.class, () -> {
            lendingService.displayLoan("test@test.com");
        });
    }

    @Test
    public void testReturnLoanWithValidLendingId() throws Exception {
        // Arrange
        Lending lending = new Lending();
        lending.setId(1);
        Book book = new Book();
        book.setId(1);
        book.setNumberExemplarActual(2);
        book.setCurrentNumberReservation(1);
        lending.setBook(book);
        when(lendingRepository.findById(1)).thenReturn(Optional.of(lending));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        // Act
        lendingService.returnLoan(1);

        // Assert
        assertEquals(3, book.getNumberExemplarActual());
        assertEquals("Terminé", lending.getStatus());
        verify(bookRepository, times(1)).saveAndFlush(book);
        verify(lendingRepository, times(1)).saveAndFlush(lending);
    }

    @Test
    public void testReturnLoanWithInvalidLendingId() throws Exception {
        // Arrange
        when(lendingRepository.findById(1)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(Exception.class, () -> lendingService.returnLoan(1));
        verify(bookRepository, never()).saveAndFlush(any());
        verify(lendingRepository, never()).saveAndFlush(any());
    }

    @Test
    public void testReturnLoanWithInvalidBookId() throws Exception {
        // Arrange
        Lending lending = new Lending();
        lending.setId(1);
        Book book = new Book();
        book.setId(1);
        book.setNumberExemplarActual(2);
        lending.setBook(book);
        when(lendingRepository.findById(1)).thenReturn(Optional.of(lending));
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(Exception.class, () -> lendingService.returnLoan(1));
        verify(bookRepository, never()).saveAndFlush(any());
        verify(lendingRepository, never()).saveAndFlush(any());
    }




}
