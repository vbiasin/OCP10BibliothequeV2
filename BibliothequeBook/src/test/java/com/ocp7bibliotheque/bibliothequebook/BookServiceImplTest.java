package com.ocp7bibliotheque.bibliothequebook;

import com.ocp7bibliotheque.bibliothequebook.DAO.BookRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import java.util.*;
import com.ocp7bibliotheque.bibliothequebook.DAO.LendingRepository;
import com.ocp7bibliotheque.bibliothequebook.DAO.LibraryRepository;
import com.ocp7bibliotheque.bibliothequebook.DAO.ReservationRepository;
import com.ocp7bibliotheque.bibliothequebook.Entites.Book;
import com.ocp7bibliotheque.bibliothequebook.Entites.Lending;
import com.ocp7bibliotheque.bibliothequebook.Entites.Library;
import com.ocp7bibliotheque.bibliothequebook.Entites.Reservation;
import com.ocp7bibliotheque.bibliothequebook.Services.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private LendingRepository lendingRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private LibraryRepository libraryRepository;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Remove book with existing ID")
    void removeBookWithExistingId() throws Exception {
        int bookId = 1;
        Book book = new Book();
        book.setId(bookId);
        List<Lending> listLoans = new ArrayList<>();
        List<Reservation> listReservations = new ArrayList<>();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(lendingRepository.findByBook(book)).thenReturn(listLoans);
        when(reservationRepository.findByBook(book)).thenReturn(listReservations);

        bookService.removeBook(bookId);

        verify(bookRepository, times(1)).delete(book);

    }

    @Test
    @DisplayName("Remove book with non-existing ID")
    void removeBookWithNonExistingId() {
        int bookId = 1;
        Book book = new Book();
        book.setId(bookId);
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Assertions.assertThrows(Exception.class, () -> {
            bookService.removeBook(bookId);
        });

        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    public void testAddBook_whenLibraryExistsAndValidBookProvided() throws Exception {
        // Arrange
        int libraryId = 1;
        Library library = new Library();
        when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
        Book book = new Book();
        book.setNumberExemplarTotal(3);
        book.setLibrary(library);

        // Act
        Book result = bookService.addBook(libraryId, book);

        // Assert
        verify(bookRepository, times(1)).save(book);
        Assertions.assertEquals(library, book.getLibrary());

    }

    @Test
    public void testAddBook_whenLibraryDoesNotExist_throwException() throws Exception {
        // Arrange
        int libraryId = 2;
        when(libraryRepository.findById(libraryId)).thenReturn(Optional.empty());
        Book book = new Book();
        book.setNumberExemplarTotal(1);

        // Act & Assert
        assertThrows(Exception.class, () -> bookService.addBook(libraryId, book));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void testAddBook_whenNumberOfExemplarIsNotPositive_throwException() throws Exception {
        // Arrange
        int libraryId = 3;
        Library library = new Library();
        when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
        Book book = new Book();
        book.setNumberExemplarTotal(0);

        // Act & Assert
        assertThrows(Exception.class, () -> bookService.addBook(libraryId, book));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void testModifyBook_whenBookExistsAndPositiveNumberOfExemplarProvided() throws Exception {
        // Arrange
        int bookId = 1;
        Book book = new Book();
        book.setNumberExemplarTotal(2);
        book.setId(bookId);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        int newNumberOfExemplar = 3;

        // Act
        bookService.modifyBook(bookId, newNumberOfExemplar);

        // Assert
        verify(bookRepository, times(1)).saveAndFlush(book);
        assertEquals(newNumberOfExemplar, book.getNumberExemplarTotal());
    }

    @Test
    public void testModifyBook_whenBookDoesNotExist_throwException() {
        // Arrange
        int bookId = 2;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        int newNumberOfExemplar = 1;

        // Act & Assert
        assertThrows(Exception.class, () -> bookService.modifyBook(bookId, newNumberOfExemplar));
        verify(bookRepository, never()).saveAndFlush(any(Book.class));
    }

    @Test
    public void testModifyBook_whenNumberOfExemplarIsNotPositive_throwException() {
        // Arrange
        int bookId = 3;
        Book book = new Book();
        book.setNumberExemplarTotal(2);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        int newNumberOfExemplar = -1;

        // Act & Assert
        assertThrows(Exception.class, () -> bookService.modifyBook(bookId, newNumberOfExemplar));
        verify(bookRepository, never()).saveAndFlush(any(Book.class));
    }
    @Test
    public void testSearchBookWithNoDuplicateResults() throws Exception {
        // Given
        String title = "Java";
        String author = "Doe";
        List<Book> books = Arrays.asList(
                new Book( "Java 101", "John Doe"),
                new Book("Java for Dummies", "Jane Smith"),
                new Book("Programming in Java", "John Doe")
        );
        when(bookRepository.findByTitleContainsOrAuthorContains(title, author)).thenReturn(books);

        // When
        List<Book> result = bookService.searchBook(title, author);

        // Then
        assertEquals(3, result.size());
        Assertions.assertTrue(result.containsAll(books));
    }

    @Test
    public void testSearchBookWithDuplicateResults() throws Exception {
        // Given
        String title = "Java";
        String author = "Doe";
        List<Book> books = Arrays.asList(
                new Book("Java 101", "John Doe"),
                new Book("Java 101", "John Doe"),
                new Book("Programming in Java", "John Doe")
        );
        when(bookRepository.findByTitleContainsOrAuthorContains(title, author)).thenReturn(books);

        // When
        List<Book> result = bookService.searchBook(title, author);

        // Then
        assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(books.get(0)));
        Assertions.assertTrue(result.contains(books.get(2)));
    }

    @Test
    public void testSearchBookWithEmptyResults() throws Exception {
        // Given
        String title = "Java";
        String author = "Doe";
        List<Book> books = Collections.emptyList();
        when(bookRepository.findByTitleContainsOrAuthorContains(title, author)).thenReturn(books);
        when(bookRepository.findAll()).thenReturn(books);

        // When
        List<Book> result = bookService.searchBook(title, author);

        // Then
        Assertions.assertTrue(result.isEmpty());
    }
}
