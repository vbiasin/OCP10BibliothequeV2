package com.ocp7bibliotheque.bibliothequeadministration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.Role;
import com.ocp7bibliotheque.bibliothequeadministration.Entites.UserAccount;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.RoleRepository;
import com.ocp7bibliotheque.bibliothequeadministration.DAO.UserAccountRepository;
import com.ocp7bibliotheque.bibliothequeadministration.Services.RoleServiceImpl;

import static org.mockito.Mockito.*;

public class RoleServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private RoleRepository roleRepository;


    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);


    }

    @Test
    void testAddRoleToUserAccountSuccess() throws Exception {
        String mail = "test@example.com";
        int idRole = 1;
        Role role = new Role("ADMIN");
        role.setId(1);
        UserAccount userAccount = new UserAccount(mail, "password");
        Collection<Role> roles = new HashSet<>();
        roles.add(new Role("USER"));
        userAccount.setRoles(roles);
        userAccount.setId(1);

        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(userAccount));
        when(roleRepository.findById(idRole)).thenReturn(Optional.of(role));
        when(userAccountRepository.saveAndFlush(userAccount)).thenReturn(userAccount);
        roleService=new RoleServiceImpl(userAccountRepository,roleRepository);
        roleService.addRoleToUserAccount(mail, idRole);

        Assertions.assertTrue(userAccount.getRoles().contains(role));
    }

    @Test
    void testAddRoleToUserAccountThrowsExceptionWhenUserNotFound() {
        String mail = "test@example.com";
        int idRole = 1;

        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.empty());

        Assertions.assertThrows(Exception.class, () -> roleService.addRoleToUserAccount(mail, idRole));
    }

    @Test
    void testAddRoleToUserAccountThrowsExceptionWhenRoleNotFound() {
        String mail = "test@example.com";
        int idRole = 1;

        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(new UserAccount( mail, "password")));
        when(roleRepository.findById(idRole)).thenReturn(Optional.empty());

        Assertions.assertThrows(Exception.class, () -> roleService.addRoleToUserAccount(mail, idRole));
    }

    @Test
    void testAddRoleToUserAccountDoesNotAddRoleIfAlreadyExists() throws Exception {
        String mail = "test@example.com";
        int idRole = 1;
        Role role = new Role("ADMIN");
        UserAccount userAccount = new UserAccount( mail, "password");
        Collection<Role> roles = new HashSet<>();
        roles.add(new Role("USER"));
        roles.add(role);
        userAccount.setRoles(roles);

        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(userAccount));
        when(roleRepository.findById(idRole)).thenReturn(Optional.of(role));
        when(userAccountRepository.saveAndFlush(userAccount)).thenReturn(userAccount);
        roleService=new RoleServiceImpl(userAccountRepository,roleRepository);
        roleService.addRoleToUserAccount(mail, idRole);

        Assertions.assertEquals(2, userAccount.getRoles().size());
    }

    @Test
    void removeRoleFromUserAccount_userNotFound_throwException() {
        // arrange
        String mail = "unknown@mail.com";
        int idRole = 1;
        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.empty());
        roleService=new RoleServiceImpl(userAccountRepository,roleRepository);

        // act and assert
        Assertions.assertThrows(Exception.class, () -> roleService.removeRoleFromUserAccount(mail, idRole), "L'utilisateur spécifié n'existe pas !");
    }

    @Test
    void removeRoleFromUserAccount_roleNotFound_throwException() {
        // arrange
        String mail = "user@mail.com";
        int idRole = 2;
        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(new UserAccount()));
        when(roleRepository.findById(idRole)).thenReturn(Optional.empty());
        roleService=new RoleServiceImpl(userAccountRepository,roleRepository);

        // act and assert
        Assertions.assertThrows(Exception.class, () -> roleService.removeRoleFromUserAccount(mail, idRole), "Aucun rôle ne correspond à cet identifiant !");
    }

    @Test
    void removeRoleFromUserAccount_roleNotAssigned_nothingChanges() throws Exception {
        // arrange
        String mail = "user@mail.com";
        int idRole = 1;
        Role role = new Role("ADMIN");
        role.setId(idRole);
        Collection<Role> roles = new HashSet<>();
        roles.add(role);
        UserAccount userAccount = new UserAccount( mail, "password");
        userAccount.setRoles(roles);
        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(userAccount));
        when(roleRepository.findById(idRole)).thenReturn(Optional.of(role));
        roleService=new RoleServiceImpl(userAccountRepository,roleRepository);

        // act
        roleService.removeRoleFromUserAccount(mail, idRole);

        // assert
        verify(roleRepository, never()).saveAndFlush(any());
    }

    @Test
    void removeRoleFromUserAccount_roleAssigned_roleRemoved() throws Exception {
        // arrange
        String mail = "user@mail.com";
        int idRole = 1;
        Role role = new Role();
        role.setId(idRole);
        role.setName("ROLE_ADMIN");
        Collection<Role> roles = new ArrayList<>();
        roles.add(role);
        UserAccount userAccount = new UserAccount();
        userAccount.setRoles(roles);
        when(userAccountRepository.findByMail(mail)).thenReturn(Optional.of(userAccount));
        when(roleRepository.findById(idRole)).thenReturn(Optional.of(role));
        roleService=new RoleServiceImpl(userAccountRepository,roleRepository);

        // act
        roleService.removeRoleFromUserAccount(mail, idRole);

        // assert
        verify(userAccountRepository, times(1)).saveAndFlush(userAccount);
        Assertions.assertFalse(userAccount.getRoles().contains(role));
    }



}

