package com.ocp7bibliotheque.bibliothequeadministration;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.BookRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.LibraryRepository;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.Book;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.Library;
import com.ocp7bibliotheque.bibliothequeadministration.Services.LibraryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class LibraryServiceImplTest {

    @Mock
    private LibraryRepository libraryRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private LibraryServiceImpl libraryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void removeLibrary_libraryNotFound_throwException() {
        // arrange
        int idLibrary = 1;
        when(libraryRepository.findById(idLibrary)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(Exception.class, () -> libraryService.removeLibrary(idLibrary), "Cette Bibliotheque n'existe pas !");
    }

    @Test
    void removeLibrary_libraryFound_deleteLibrary() throws Exception {
        // arrange
        int idLibrary = 1;
        Library library = new Library();
        library.setId(idLibrary);
        when(libraryRepository.findById(idLibrary)).thenReturn(Optional.of(library));
        when(bookRepository.findByLibrary(library)).thenReturn(new ArrayList<Book>());

        // act
        libraryService.removeLibrary(idLibrary);

        // assert
        verify(libraryRepository, times(1)).delete(library);
    }

    @Test
    void removeLibrary_libraryFound_deleteBooks() throws Exception {
        // arrange
        int idLibrary = 1;
        Library library = new Library();
        library.setId(idLibrary);
        Book book1 = new Book();
        book1.setLibrary(library);
        Book book2 = new Book();
        book2.setLibrary(library);
        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);
        when(libraryRepository.findById(idLibrary)).thenReturn(Optional.of(library));
        when(bookRepository.findByLibrary(library)).thenReturn(books);

        // act
        libraryService.removeLibrary(idLibrary);

        // assert
        verify(bookRepository, times(2)).delete(any());
    }

    @Test
    void addLibrary_libraryAlreadyExists_throwException() {
        // arrange
        Library library = new Library();
        library.setName("Library 1");
        library.setAddress("Address 1");
        when(libraryRepository.findByNameAndAddress(library.getName(), library.getAddress())).thenReturn(Optional.of(library));

        // act and assert
        assertThrows(Exception.class, () -> libraryService.addLibrary(library), "Cette Bibliotheque existe déjà !");
    }

    @Test
    void addLibrary_libraryDoesNotExist_saveLibrary() throws Exception {
        // arrange
        Library library = new Library();
        library.setName("Library 1");
        library.setAddress("Address 1");
        when(libraryRepository.findByNameAndAddress(library.getName(), library.getAddress())).thenReturn(Optional.empty());
        when(libraryRepository.save(library)).thenReturn(library);

        // act
        Library savedLibrary = libraryService.addLibrary(library);

        // assert
        assertNotNull(savedLibrary);
        assertEquals(library.getName(), savedLibrary.getName());
        assertEquals(library.getAddress(), savedLibrary.getAddress());
        verify(libraryRepository, times(1)).findByNameAndAddress(library.getName(), library.getAddress());
        verify(libraryRepository, times(1)).save(library);
    }

    @Test
    void searchLibrary_nameAndAddressProvided_returnMatchingLibraries() throws Exception {
        // arrange
        String name = "Library";
        String address = "Address";
        Library library1 = new Library();
        library1.setId(1);
        library1.setName("Library 1");
        library1.setAddress("Address 1");
        Library library2 = new Library();
        library2.setId(2);
        library2.setName("Library 2");
        library2.setAddress("Address 2");
        List<Library> libraries = new ArrayList<>();
        libraries.add(library1);
        libraries.add(library2);
        when(libraryRepository.findByNameContainsOrAddressContains(name, address)).thenReturn(libraries);

        // act
        List<Library> searchedLibraries = libraryService.searchLibrary(name, address);

        // assert
        assertNotNull(searchedLibraries);
        assertEquals(2, searchedLibraries.size());
        assertEquals(library1.getId(), searchedLibraries.get(0).getId());
        assertEquals(library1.getName(), searchedLibraries.get(0).getName());
        assertEquals(library1.getAddress(), searchedLibraries.get(0).getAddress());
        assertEquals(library2.getId(), searchedLibraries.get(1).getId());
        assertEquals(library2.getName(), searchedLibraries.get(1).getName());
        assertEquals(library2.getAddress(), searchedLibraries.get(1).getAddress());
        verify(libraryRepository, times(1)).findByNameContainsOrAddressContains(name, address);
    }

    @Test
    void searchLibrary_nameAndAddressNotProvided_returnAllLibraries() throws Exception {
        // arrange
        List<Library> libraries = new ArrayList<>();
        libraries.add(new Library());
        when(libraryRepository.findAll()).thenReturn(libraries);

        // act
        List<Library> searchedLibraries = libraryService.searchLibrary(null, null);

        // assert
        assertNotNull(searchedLibraries);
        assertEquals(1, searchedLibraries.size());
        verify(libraryRepository, times(1)).findAll();
    }


}
