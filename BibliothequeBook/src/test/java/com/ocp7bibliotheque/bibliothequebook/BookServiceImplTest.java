package com.ocp7bibliotheque.bibliothequebook;
import com.ocp7bibliotheque.bibliothequebook.DAO.BookRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.*;

import com.ocp7bibliotheque.bibliothequebook.DAO.LendingRepository;
import com.ocp7bibliotheque.bibliothequebook.DAO.LibraryRepository;
import com.ocp7bibliotheque.bibliothequebook.Entites.Book;
import com.ocp7bibliotheque.bibliothequebook.Entites.Lending;
import com.ocp7bibliotheque.bibliothequebook.Entites.Library;
import com.ocp7bibliotheque.bibliothequebook.Services.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
    private LibraryRepository libraryRepository;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRemoveBook_whenBookExistsAndHasNoLendings_removeBookAndReturnVoid() throws Exception {
        // Arrange
        int bookId = 1;
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(lendingRepository.findByBook(book)).thenReturn(new ArrayList<Lending>());

        // Act
        bookService.removeBook(bookId);

        // Assert
        verify(lendingRepository, never()).delete(any(Lending.class));
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    public void testRemoveBook_whenBookExistsAndHasLendings_deleteLendingsAndRemoveBook() throws Exception {
        // Arrange
        int bookId = 2;
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        List<Lending> lendings = new ArrayList<>();
        lendings.add(new Lending());
        lendings.add(new Lending());
        when(lendingRepository.findByBook(book)).thenReturn(lendings);

        // Act
        bookService.removeBook(bookId);

        // Assert
        verify(lendingRepository, times(lendings.size())).delete(any(Lending.class));
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    public void testRemoveBook_whenBookDoesNotExist_throwException() {
        // Arrange
        int bookId = 3;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> bookService.removeBook(bookId));
        verify(lendingRepository, never()).delete(any(Lending.class));
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    public void testAddBook_whenLibraryExistsAndValidBookProvided() throws Exception {
        // Arrange
        int libraryId = 1;
        Library library = new Library();
        when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
        Book book = new Book();
        book.setNumberExemplar(3);
        book.setLibrary(library);

        // Act
        Book result = bookService.addBook(libraryId, book);

        // Assert
        verify(bookRepository, times(1)).save(book);
        assertEquals(library, book.getLibrary());

    }

    @Test
    public void testAddBook_whenLibraryDoesNotExist_throwException() throws Exception {
        // Arrange
        int libraryId = 2;
        when(libraryRepository.findById(libraryId)).thenReturn(Optional.empty());
        Book book = new Book();
        book.setNumberExemplar(1);

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
        book.setNumberExemplar(0);

        // Act & Assert
        assertThrows(Exception.class, () -> bookService.addBook(libraryId, book));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void testModifyBook_whenBookExistsAndPositiveNumberOfExemplarProvided() throws Exception {
        // Arrange
        int bookId = 1;
        Book book = new Book();
        book.setNumberExemplar(2);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        int newNumberOfExemplar = 3;

        // Act
        Book result = bookService.modifyBook(bookId, newNumberOfExemplar);

        // Assert
        verify(bookRepository, times(1)).saveAndFlush(book);
        assertEquals(newNumberOfExemplar, book.getNumberExemplar());
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
        book.setNumberExemplar(2);
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
        assertTrue(result.containsAll(books));
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
        assertTrue(result.contains(books.get(0)));
        assertTrue(result.contains(books.get(2)));
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
        assertTrue(result.isEmpty());
    }
}
