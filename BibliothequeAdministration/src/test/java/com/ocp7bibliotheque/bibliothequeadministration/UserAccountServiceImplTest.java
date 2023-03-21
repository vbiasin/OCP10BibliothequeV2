package com.ocp7bibliotheque.bibliothequeadministration;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.ocp7bibliotheque.bibliothequeadministration.DAO.ContactRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.LendingRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.RoleRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.UserAccountRepository;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.*;
import com.ocp7bibliotheque.bibliothequeadministration.Services.UserAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class UserAccountServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private LendingRepository lendingRepository;

    @Mock
    private static BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Contact contact1 = new Contact("John", "Doe", "john.doe@example.com");
        Contact contact2 = new Contact("Jane", "Doe", "jane.doe@example.com");
        Contact contact3 = new Contact("Jim", "Smith", "jim.smith@example.com");

        UserAccount userAccount1 = new UserAccount("user1@example.com", "password");
        userAccount1.setContact(contact1);
        UserAccount userAccount2 = new UserAccount("user2@example.com", "password");
        userAccount2.setContact(contact2);
        UserAccount userAccount3 = new UserAccount("user3@example.com", "password");
        userAccount3.setContact(contact3);

        List<Contact> contacts = Arrays.asList(contact1, contact2, contact3);
        List<UserAccount> userAccounts = Arrays.asList(userAccount1, userAccount2, userAccount3);

        when(contactRepository.findByLastNameOrFirstName("Doe", null)).thenReturn(Arrays.asList(contact1, contact2));
        when(contactRepository.findByLastNameOrFirstName(null, "Jim")).thenReturn(Arrays.asList(contact3));
        when(contactRepository.findByLastNameOrFirstName("Doe", "Jane")).thenReturn(Arrays.asList(contact2));

        when(userAccountRepository.findByMail("user1@example.com")).thenReturn(Optional.of(userAccount1));
        when(userAccountRepository.findByMail("user2@example.com")).thenReturn(Optional.of(userAccount2));
        when(userAccountRepository.findByMail("user3@example.com")).thenReturn(Optional.of(userAccount3));

        when(userAccountRepository.findByContact(contact1)).thenReturn(Optional.of(userAccount1));
        when(userAccountRepository.findByContact(contact2)).thenReturn(Optional.of(userAccount2));
        when(userAccountRepository.findByContact(contact3)).thenReturn(Optional.of(userAccount3));

        when(userAccountRepository.findAll()).thenReturn(userAccounts);
    }
    @Test
    @DisplayName("Should register user account successfully")
    public void shouldRegisterUserAccountSuccessfully() throws Exception {
        // Given
        Role role = new Role();
        role.setId(1);
        role.setName("USER");
        Optional<Role> defaultRole = Optional.of(role);

        UserAccount account = new UserAccount();
        account.setId(1);
        account.setMail("test@test.com");
        account.setPassword("password");

        Optional<UserAccount> newUser = Optional.empty();

        Collection<Role> roles = new ArrayList<Role>();
        roles.add(defaultRole.get());

        when(roleRepository.findByName("USER")).thenReturn(defaultRole);
        when(userAccountRepository.findByMail("test@test.com")).thenReturn(newUser);
        when(bCryptPasswordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userAccountRepository.save(account)).thenReturn(account);

        // When
        UserAccount savedAccount = userAccountService.register(account);

        // Then
        assertNotNull(savedAccount);
        assertEquals(account.getId(), savedAccount.getId());
        assertEquals(account.getMail(), savedAccount.getMail());
        assertEquals("encodedPassword", savedAccount.getPassword());
        assertEquals(roles, savedAccount.getRoles());
    }

    @Test
    @DisplayName("Should throw an exception when default role is not found")
    public void shouldThrowExceptionWhenDefaultRoleNotFound() throws Exception {
        // Given
        Optional<Role> defaultRole = Optional.empty();

        UserAccount account = new UserAccount();
        account.setMail("test@test.com");
        account.setPassword("password");

        when(roleRepository.findByName("USER")).thenReturn(defaultRole);

        // When, Then
        assertThrows(Exception.class, () -> userAccountService.register(account));
    }

    @Test
    @DisplayName("Should throw an exception when user with the same mail already exists")
    public void shouldThrowExceptionWhenUserWithSameMailExists() throws Exception {
        // Given
        Role role = new Role();
        role.setId(1);
        role.setName("USER");
        Optional<Role> defaultRole = Optional.of(role);

        UserAccount account = new UserAccount();
        account.setMail("test@test.com");
        account.setPassword("password");

        Optional<UserAccount> newUser = Optional.of(account);

        when(roleRepository.findByName("USER")).thenReturn(defaultRole);
        when(userAccountRepository.findByMail("test@test.com")).thenReturn(newUser);

        // When, Then
        assertThrows(Exception.class, () -> userAccountService.register(account));
    }

    @Test
    void testIsValidWithCorrectCredentials() throws Exception {
        // Arrange
        String mail = "user@test.com";
        String password = "password";
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        UserAccount userAccount = new UserAccount(mail, encodedPassword);
        userAccount.setActive(true);
        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(userAccount));

        // Act
        boolean result = userAccountService.isValid(userAccount);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValidWithIncorrectCredentials() {
        // Arrange
        String mail = "user@test.com";
        String password = "password";
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        UserAccount userAccount = new UserAccount(mail, encodedPassword);
        userAccount.setActive(true);
        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(userAccount));

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userAccountService.isValid(new UserAccount(mail, "wrong_password"));
        });
    }

    @Test
    void testIsValidWithNonexistentUser() {
        // Arrange
        String mail = "user@test.com";
        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userAccountService.isValid(new UserAccount(mail, "password"));
        });
    }

    @Test
    void testGetUserAccount() throws Exception {
        // given
        String mail = "test@test.com";
        UserAccount userAccount = new UserAccount();
        userAccount.setId(1);
        userAccount.setMail(mail);
        Optional<UserAccount> optionalUserAccount = Optional.of(userAccount);
        when(userAccountRepository.findByMail(mail)).thenReturn(optionalUserAccount);

        // when
        UserAccount result = userAccountService.getUserAccount(mail);

        // then
        assertEquals(userAccount, result);
    }

    @Test
    void testGetUserAccountWhenMailNotFound() {
        // given
        String mail = "test@test.com";
        Optional<UserAccount> optionalUserAccount = Optional.empty();
        when(userAccountRepository.findByMail(mail)).thenReturn(optionalUserAccount);

        // when and then
        assertThrows(Exception.class, () -> {
            userAccountService.getUserAccount(mail);
        });
    }

    @Test
    void testRemoveUserAccount() throws Exception {
        // création d'un utilisateur
        UserAccount userAccount = new UserAccount();
        userAccount.setMail("test@example.com");
        userAccount.setPassword("password");
        userAccount.setActive(true);
        userAccount.setId(1);
        userAccountRepository.save(userAccount);

        // création d'un emprunt associé à l'utilisateur
        Lending lending = new Lending();
        lending.setUserAccount(userAccount);
        lending.setBook(new Book());
        lending.setStartDate(LocalDateTime.now());
        lending.setEndDate(LocalDateTime.now().plusDays(7));
        lendingRepository.save(lending);

        when(userAccountRepository.findById(1)).thenReturn(Optional.of(userAccount));

        // appel de la méthode à tester
        userAccountService.removeUserAccount(userAccount.getId());

        // vérification que l'utilisateur a été supprimé
        Optional<UserAccount> deletedUserAccount = userAccountRepository.findById(1);
        assertTrue(deletedUserAccount.isEmpty());

        // vérification que l'emprunt associé à l'utilisateur a été supprimé
        Optional<Lending> deletedLending = lendingRepository.findById(lending.getId());
        assertTrue(deletedLending.isEmpty());
    }

    @Test
    void testRemoveUserAccountNonExisting() {
        // appel de la méthode à tester avec un ID inexistant
        assertThrows(Exception.class, () -> userAccountService.removeUserAccount(100));
    }

    @Test
    void testSearchUserAccountByMail() throws Exception {
        List<UserAccount> result = userAccountService.searchUserAccount("user1@example.com", null, null);
        assertEquals(1, result.size());
        assertEquals("user1@example.com", result.get(0).getMail());
    }

    @Test
    void testSearchUserAccountByLastName() throws Exception {
        List<UserAccount> result = userAccountService.searchUserAccount(null, "Doe", null);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getMail().equals("user1@example.com")));
        assertTrue(result.stream().anyMatch(u -> u.getMail().equals("user2@example.com")));
    }

    @Test
    void testSearchUserAccountByFirstName() throws Exception {
        List<UserAccount> result = userAccountService.searchUserAccount("defaultValue@gmail.com", "defaultLastName", "Jim");
        assertEquals(1, result.size());
        assertEquals("user3@example.com", result.get(0).getMail());
    }

    @Test
    void testSearchUserAccountByFullName() throws Exception {
        // Create test contacts and user accounts
        Contact contact1 = new Contact("John", "Doe", "johndoe@test.com");
        Contact contact2 = new Contact("Jane", "Doe",  "janedoe@test.com");
        contactRepository.save(contact1);
        contactRepository.save(contact2);

        UserAccount userAccount1 = new UserAccount("johndoe@test.com", "password");
        UserAccount userAccount2 = new UserAccount("janedoe@test.com", "password");
        userAccount1.setContact(contact1);
        userAccount2.setContact(contact2);
        userAccountRepository.save(userAccount1);
        userAccountRepository.save(userAccount2);

        // Test search by full name
        List<UserAccount> searchResults = userAccountService.searchUserAccount(null, "Doe", "John");
        assertEquals(1, searchResults.size());
        assertEquals(userAccount1.getId(), searchResults.get(0).getId());

        searchResults = userAccountService.searchUserAccount("", "Doe", "Jane");
        assertEquals(1, searchResults.size());
        assertEquals(userAccount2.getId(), searchResults.get(0).getId());

        // Test search by full name not found
        searchResults = userAccountService.searchUserAccount("", "Doe", "Mary");
        assertTrue(searchResults.isEmpty());

        // Clean up
        userAccountRepository.delete(userAccount1);
        userAccountRepository.delete(userAccount2);
        contactRepository.delete(contact1);
        contactRepository.delete(contact2);
    }

}
