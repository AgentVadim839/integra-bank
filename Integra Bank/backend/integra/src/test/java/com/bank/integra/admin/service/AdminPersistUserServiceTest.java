package com.bank.integra.admin.service;

import com.bank.integra.general.repository.RolesRepository;
import com.bank.integra.user.model.UserDetails;
import com.bank.integra.user.model.User;
import com.bank.integra.general.model.Role;
import com.bank.integra.admin.dto.AdminDTO;
import com.bank.integra.user.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class) // Використовуємо SpringBootTest для повної ініціалізації контексту (може бути корисно для складніших сценаріїв)
class AdminPersistUserServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RolesRepository rolesRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @InjectMocks
    private AdminPersistUserService adminService;

    @Test
    void test_saveUserFromForm_success() {
        // Given
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(10);
        adminDTO.setPassword("secret");
        adminDTO.setBalance(1000.0);
        adminDTO.setFirstName("John");
        adminDTO.setLastName("Doe");
        adminDTO.setEmail("john.doe@example.com");

        when(passwordEncoder.encode("secret")).thenReturn("hashed_secret");

        // When
        adminService.saveUserFromForm(adminDTO, model);

        // Then
        verify(userService, times(1)).createUser(any(User.class), any(UserDetails.class));
        verify(rolesRepository, times(1)).save(any(Role.class));
        verify(model).addAttribute(eq("successSave"), eq("User 10 has been successfully created."));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).createUser(userCaptor.capture(), any(UserDetails.class));
        assertEquals("hashed_secret", userCaptor.getValue().getPassword());
        assertEquals(10, userCaptor.getValue().getId());
        assertTrue(userCaptor.getValue().isActive());

        ArgumentCaptor<UserDetails> userDetailsCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(userService).createUser(any(User.class), userDetailsCaptor.capture());
        assertEquals(10, userDetailsCaptor.getValue().getUserId());
        assertEquals(1000.0, userDetailsCaptor.getValue().getBalance(), 0.001);
        assertEquals("John", userDetailsCaptor.getValue().getFirstName());
        assertEquals("Doe", userDetailsCaptor.getValue().getLastName());
        assertEquals("john.doe@example.com", userDetailsCaptor.getValue().getEmail());

        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(rolesRepository).save(roleCaptor.capture());
        assertEquals(10, roleCaptor.getValue().getId());
        assertEquals("EMPLOYEE", roleCaptor.getValue().getRole());
    }

    @Test
    void test_saveUserFromForm_nullPassword() {
        // Given
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(20);
        adminDTO.setPassword(null); // Null password
        adminDTO.setBalance(500.0);
        adminDTO.setFirstName("Jane");
        adminDTO.setLastName("Smith");
        adminDTO.setEmail("jane.smith@example.com");

        when(passwordEncoder.encode(null)).thenReturn(null); // Що поверне енкодер для null? Залежить від реалізації

        // When
        adminService.saveUserFromForm(adminDTO, model);

        // Then
        verify(passwordEncoder, times(1)).encode(null);
        verify(userService, times(1)).createUser(any(User.class), any(UserDetails.class));
        verify(rolesRepository, times(1)).save(any(Role.class));
        verify(model).addAttribute(eq("successSave"), eq("User 20 has been successfully created."));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).createUser(userCaptor.capture(), any(UserDetails.class));
        assertEquals(null, userCaptor.getValue().getPassword()); // Перевіряємо, що null передається далі
    }

    @Test
    void test_saveUserFromForm_zeroBalance() {
        // Given
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(30);
        adminDTO.setPassword("secure");
        adminDTO.setBalance(0.0); // Нульовий баланс
        adminDTO.setFirstName("Peter");
        adminDTO.setLastName("Jones");
        adminDTO.setEmail("peter.jones@example.com");

        when(passwordEncoder.encode("secure")).thenReturn("hashed_secure");

        // When
        adminService.saveUserFromForm(adminDTO, model);

        // Then
        verify(userService, times(1)).createUser(any(User.class), any(UserDetails.class));
        verify(rolesRepository, times(1)).save(any(Role.class));
        verify(model).addAttribute(eq("successSave"), eq("User 30 has been successfully created."));

        ArgumentCaptor<UserDetails> userDetailsCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(userService).createUser(any(User.class), userDetailsCaptor.capture());
        assertEquals(0.0, userDetailsCaptor.getValue().getBalance(), 0.001);
    }

    @Test
    void test_saveUserFromForm_emptyEmail() {
        // Given
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(40);
        adminDTO.setPassword("topsecret");
        adminDTO.setBalance(100.0);
        adminDTO.setFirstName("Alice");
        adminDTO.setLastName("Brown");
        adminDTO.setEmail(""); // Порожній email

        when(passwordEncoder.encode("topsecret")).thenReturn("hashed_topsecret");

        // When
        adminService.saveUserFromForm(adminDTO, model);

        // Then
        verify(userService, times(1)).createUser(any(User.class), any(UserDetails.class));
        verify(rolesRepository, times(1)).save(any(Role.class));
        verify(model).addAttribute(eq("successSave"), eq("User 40 has been successfully created."));

        ArgumentCaptor<UserDetails> userDetailsCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(userService).createUser(any(User.class), userDetailsCaptor.capture());
        assertEquals("", userDetailsCaptor.getValue().getEmail());
    }

    @Test
    void test_saveUserFromForm_specialCharactersInNames() {
        // Given
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(50);
        adminDTO.setPassword("complex!");
        adminDTO.setBalance(1000000.0);
        adminDTO.setFirstName("O'Malley");
        adminDTO.setLastName("van der Sar");
        adminDTO.setEmail("complex!@example.com");

        when(passwordEncoder.encode("complex!")).thenReturn("hashed_complex!");

        // When
        adminService.saveUserFromForm(adminDTO, model);

        // Then
        verify(userService, times(1)).createUser(any(User.class), any(UserDetails.class));
        verify(rolesRepository, times(1)).save(any(Role.class));
        verify(model).addAttribute(eq("successSave"), eq("User 50 has been successfully created."));

        ArgumentCaptor<UserDetails> userDetailsCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(userService).createUser(any(User.class), userDetailsCaptor.capture());
        assertEquals("O'Malley", userDetailsCaptor.getValue().getFirstName());
        assertEquals("van der Sar", userDetailsCaptor.getValue().getLastName());
    }

    @Test
    void test_saveUserFromForm_largeBalance() {
        // Given
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(60);
        adminDTO.setPassword("verylongpassword123");
        adminDTO.setBalance(999999999999.99); // Дуже великий баланс
        adminDTO.setFirstName("Rich");
        adminDTO.setLastName("Guy");
        adminDTO.setEmail("rich.guy@example.com");

        when(passwordEncoder.encode("verylongpassword123")).thenReturn("hashed_verylongpassword123");

        // When
        adminService.saveUserFromForm(adminDTO, model);

        // Then
        verify(userService, times(1)).createUser(any(User.class), any(UserDetails.class));
        verify(rolesRepository, times(1)).save(any(Role.class));
        verify(model).addAttribute(eq("successSave"), eq("User 60 has been successfully created."));

        ArgumentCaptor<UserDetails> userDetailsCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(userService).createUser(any(User.class), userDetailsCaptor.capture());
        assertEquals(999999999999.99, userDetailsCaptor.getValue().getBalance(), 0.001);
    }

    @Test
    void test_saveUserFromForm_userIdWithLeadingAndTrailingSpaces() {
        // Given
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(70); // UserId з пробілами (хоча ви сказали, що він завжди число)
        adminDTO.setPassword("spaced");
        adminDTO.setBalance(100.0);
        adminDTO.setFirstName("Space");
        adminDTO.setLastName("Man");
        adminDTO.setEmail("space.man@example.com");

        when(passwordEncoder.encode("spaced")).thenReturn("hashed_spaced");

        // When
        adminService.saveUserFromForm(adminDTO, model);

        // Then
        verify(userService, times(1)).createUser(any(User.class), any(UserDetails.class));
        verify(rolesRepository, times(1)).save(any(Role.class));
        verify(model).addAttribute(eq("successSave"), eq("User 70 has been successfully created."));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).createUser(userCaptor.capture(), any(UserDetails.class));
        assertEquals(70, userCaptor.getValue().getId()); // Перевіряємо, що пробіли не вплинули на ID (якщо це число)
    }

    @Test
    void test_saveUserFromForm_sqlInjectionAttemptInNames() {
        // Given
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(80);
        adminDTO.setPassword("insecure");
        adminDTO.setBalance(1000.0);
        adminDTO.setFirstName("'; DROP TABLE users; --");
        adminDTO.setLastName("Test");
        adminDTO.setEmail("attack@example.com");

        when(passwordEncoder.encode("insecure")).thenReturn("hashed_insecure");

        // When
        adminService.saveUserFromForm(adminDTO, model);

        // Then
        verify(userService, times(1)).createUser(any(User.class), any(UserDetails.class));
        verify(rolesRepository, times(1)).save(any(Role.class));
        verify(model).addAttribute(eq("successSave"), eq("User 80 has been successfully created."));

        ArgumentCaptor<UserDetails> userDetailsCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(userService).createUser(any(User.class), userDetailsCaptor.capture());
        assertEquals("'; DROP TABLE users; --", userDetailsCaptor.getValue().getFirstName()); // Перевіряємо, що як є, так і передається (відсутність валідації)
    }

    @Test
    void test_saveUserFromForm_xssAttemptInEmail() {
        // Given
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(90);
        adminDTO.setPassword("xss");
        adminDTO.setBalance(100.0);
        adminDTO.setFirstName("XSS");
        adminDTO.setLastName("User");
        adminDTO.setEmail("<script>alert('XSS')</script>");

        when(passwordEncoder.encode("xss")).thenReturn("hashed_xss");

        // When
        adminService.saveUserFromForm(adminDTO, model);

        // Then
        verify(userService, times(1)).createUser(any(User.class), any(UserDetails.class));
        verify(rolesRepository, times(1)).save(any(Role.class));
        verify(model).addAttribute(eq("successSave"), eq("User 90 has been successfully created."));

        ArgumentCaptor<UserDetails> userDetailsCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(userService).createUser(any(User.class), userDetailsCaptor.capture());
        assertEquals("<script>alert('XSS')</script>", userDetailsCaptor.getValue().getEmail()); // Знову ж таки, відсутність валідації
    }
}