package com.ocp7bibliotheque.bibliothequeadministration;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.ContactRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.LendingRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.RoleRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.UserAccountRepository;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.*;
import com.ocp7bibliotheque.bibliothequeadministration.Services.UserAccountServiceImpl;
import org.junit.Ignore;
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
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

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

/*    @Ignore
    @Test
    void testIsValidWithCorrectCredentials() throws Exception {
        // Arrange
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String mail = "user@test.com";
        String password = "password";

        String encodedPassword = bCryptPasswordEncoder.encode(password);
        UserAccount userAccount = new UserAccount(mail,password);
        userAccount.setActive(true);
        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(userAccount));

        // Act
        boolean result = userAccountService.isValid(userAccount);

        // Assert
        assertTrue(result);
    }*/

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
       // Créer un objet UserAccount pour le test
       UserAccount userAccount = new UserAccount();
       userAccount.setId(1);

       // Créer un objet Lending pour le test
       Lending lending = new Lending();
       lending.setId(1);
       lending.setUserAccount(userAccount);

       // Créer une liste contenant l'emprunt créé ci-dessus
       List<Lending> listLoans = new ArrayList<>();
       listLoans.add(lending);

       // Configurer le mock pour renvoyer l'utilisateur et les prêts correspondants
       when(userAccountRepository.findById(userAccount.getId())).thenReturn(Optional.of(userAccount));
       when(lendingRepository.findByUserAccount(userAccount)).thenReturn(listLoans);

       // Appeler la méthode à tester
       userAccountService.removeUserAccount(userAccount.getId());

       // Vérifier que la méthode de suppression de compte utilisateur et la méthode de suppression d'emprunt ont été appelées avec les bons paramètres
       verify(userAccountRepository, times(1)).delete(userAccount);
       verify(lendingRepository, times(1)).delete(lending);
   }

    @Test
    @DisplayName("When the input list is empty, return an empty list")
    void testGetListWithNoDoublon_emptyList() throws Exception {
        List<Contact> input = new ArrayList<>();
        List<Contact> expected = new ArrayList<>();
        assertEquals(expected, userAccountService.getListWithNoDoublon(input));
    }

    @Test
    @DisplayName("When the input list has no duplicates, return the same list")
    void testGetListWithNoDoublon_noDuplicates() throws Exception {
        List<Contact> input = new ArrayList<>();
        input.add(new Contact("John", "Doe","address"));
        input.add(new Contact("Jane", "Doe","address"));
        List<Contact> expected = new ArrayList<>(input);
        assertEquals(expected, userAccountService.getListWithNoDoublon(input));
    }

    @Test
    @DisplayName("When the input list has duplicates, return the list without duplicates")
    void testGetListWithNoDoublon_withDuplicates() throws Exception {
        List<Contact> input = new ArrayList<>();
        Contact contact1=new Contact("John", "Doe","address");
        Contact contact2=new Contact("Jane", "Doe","address");
        input.add(contact1);
        input.add(contact2);
        input.add(new Contact("John", "Doe","address"));
        List<Contact> expected = new ArrayList<>();
        expected.add(contact1);
        expected.add(contact2);
        assertEquals(expected, userAccountService.getListWithNoDoublon(input));
    }

    @Test
    public void testSearchUserAccountWithNoMatches() throws Exception {
        // Given
        Contact contact = new Contact("John", "Doe","address");
        UserAccount userAccount = new UserAccount("toto@exemple.com", "password");
        userAccount.setContact(contact);
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);
        String mail = "toto@exemple.com";
        String lastName = "Doe";
        String firstName = "Jane";
        List<Contact> emptyList = new ArrayList<>();
        when(contactRepository.findByLastNameOrFirstName(lastName, firstName)).thenReturn(emptyList);
        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(userAccount));

        // When
        List<UserAccount> result = userAccountService.searchUserAccount(mail, lastName, firstName);

        // Then
        assertTrue(result.isEmpty());
    }

/*
    @Ignore
    @Test
    public void testSearchUserAccountWithMatches() throws Exception {
        // Given
        Contact contact = new Contact("John", "Doe","address");
        UserAccount userAccount = new UserAccount("toto@exemple.com", "password");
        userAccount.setContact(contact);
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);
        String mail = "toto@exemple.com";
        String lastName = "Doe";
        String firstName = "John";
        List<Contact> matchingContacts = new ArrayList<>();
        matchingContacts.add(contact);
        when(contactRepository.findByLastNameOrFirstName(lastName, firstName)).thenReturn(matchingContacts);
        when(userAccountRepository.findByContact(contact)).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(userAccount));

        // When
        List<UserAccount> result = userAccountService.searchUserAccount(mail, lastName, firstName);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(userAccount));
    }

    @Ignore
    @Test
    public void testSearchUserAccountWithNoMatchesAndNoMail() throws Exception {
        // Given
        Contact contact = new Contact("John", "Doe","address");
        UserAccount userAccount = new UserAccount("toto@exemple.com", "password");
        userAccount.setContact(contact);
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);
        String mail = "tata@exemple.com";
        String lastName = "Smith";
        String firstName = "Jane";
        List<Contact> emptyList = new ArrayList<>();
        List<UserAccount> allUserAccounts = new ArrayList<>();
        allUserAccounts.add(userAccount);
        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.empty());
        when(contactRepository.findByLastNameOrFirstName(lastName, firstName)).thenReturn(emptyList);
        when(userAccountRepository.findAll()).thenReturn(allUserAccounts);

        // When
        List<UserAccount> result = userAccountService.searchUserAccount(mail, lastName, firstName);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(userAccount));
    }
*/


}
