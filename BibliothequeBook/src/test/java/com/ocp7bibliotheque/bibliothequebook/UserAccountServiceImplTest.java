package com.ocp7bibliotheque.bibliothequebook;
import com.ocp7bibliotheque.bibliothequebook.DAO.UserAccountRepository;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

import com.ocp7bibliotheque.bibliothequebook.Entites.Contact;
import com.ocp7bibliotheque.bibliothequebook.Entites.UserAccount;
import com.ocp7bibliotheque.bibliothequebook.Services.IUserAccountService;
import com.ocp7bibliotheque.bibliothequebook.Services.UserAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
public class UserAccountServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckUserAccountContact_whenUserAccountExistsAndHasContact_returnTrue() throws Exception {
        // Arrange
        String mail = "john.doe@example.com";
        UserAccount userAccount = new UserAccount();
        userAccount.setContact(new Contact());
        Mockito.when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(userAccount));

        // Act
        Boolean result = userAccountService.checkUserAccountContact(mail);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testCheckUserAccountContact_whenUserAccountExistsAndHasNoContact_returnFalse() throws Exception {
        // Arrange
        String mail = "jane.doe@example.com";
        UserAccount userAccount = new UserAccount();
        Mockito.when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(userAccount));

        // Act
        Boolean result = userAccountService.checkUserAccountContact(mail);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testCheckUserAccountContact_whenUserAccountDoesNotExist_throwException() {
        // Arrange
        String mail = "non.existant@example.com";
        Mockito.when(userAccountRepository.findByMail(mail)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> userAccountService.checkUserAccountContact(mail));
    }


}
