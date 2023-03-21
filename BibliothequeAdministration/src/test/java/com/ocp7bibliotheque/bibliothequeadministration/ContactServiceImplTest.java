package com.ocp7bibliotheque.bibliothequeadministration;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.Contact;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.UserAccount;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.ContactRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.UserAccountRepository;
import com.ocp7bibliotheque.bibliothequeadministration.Services.ContactServiceImpl;

public class ContactServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addContact_UserAccountNotFound_ThrowsException() throws Exception {
        // Arrange
        int idUserAccount = 1;
        Optional<UserAccount> optionalUserAccount = Optional.empty();
        when(userAccountRepository.findById(idUserAccount)).thenReturn(optionalUserAccount);

        // Act and Assert
        assertThrows(Exception.class, () -> {
           contactService.addContact(idUserAccount, new Contact());
        });
    }

    @Test
    public void addContact_UserAccountHasExistingContact_ThrowsException() throws Exception {
        // Arrange
        int idUserAccount = 1;
        UserAccount userAccount = new UserAccount();
        userAccount.setContact(new Contact());
        Optional<UserAccount> optionalUserAccount = Optional.of(userAccount);
        when(userAccountRepository.findById(idUserAccount)).thenReturn(optionalUserAccount);

        // Act and Assert
        assertThrows(Exception.class, () -> {
            contactService.addContact(idUserAccount, new Contact());
        });
    }

    @Test
    public void addContact_ValidInput_ReturnsNewContact() throws Exception {
        // Arrange
        int idUserAccount = 1;
        UserAccount userAccount = new UserAccount();
        Optional<UserAccount> optionalUserAccount = Optional.of(userAccount);
        when(userAccountRepository.findById(idUserAccount)).thenReturn(optionalUserAccount);

        Contact newContact = new Contact();
        when(contactRepository.save(newContact)).thenReturn(newContact);

        // Act
        Contact result = contactService.addContact(idUserAccount, newContact);

        // Assert
        assertEquals(newContact, result);
        assertEquals(newContact, userAccount.getContact());
        verify(contactRepository, times(1)).save(newContact);
        verify(userAccountRepository, times(1)).saveAndFlush(userAccount);
    }

    @Test
    void testModifyContactSuccess() throws Exception {
        int idContact = 1;
        Contact existingContact = new Contact("Doe", "John", "Address1");
        existingContact.setId(1);
        Contact updatedContact = new Contact("Doe", "John", "Address2");
        updatedContact.setId(1);

        when(contactRepository.findById(idContact)).thenReturn(Optional.of(existingContact));
        when(contactRepository.saveAndFlush(existingContact)).thenReturn(updatedContact);

        Contact modifiedContact = contactService.modifyContact(idContact, updatedContact);

        assertEquals(updatedContact.getAddress(), modifiedContact.getAddress());
        assertEquals(updatedContact.getLastName(), modifiedContact.getLastName());
        assertEquals(existingContact.getId(), modifiedContact.getId());
    }

    @Test
    void testModifyContactThrowsExceptionWhenContactNotFound() {
        int idContact = 1;
        Contact updatedContact = new Contact("Smith", "Stan", "Address2");
        updatedContact.setId(1);
        when(contactRepository.findById(idContact)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> contactService.modifyContact(idContact, updatedContact));
    }

}

