package com.bank.integra.admin.service;

import com.bank.integra.user.model.UserDetails;
import com.bank.integra.user.model.User;
import com.bank.integra.admin.dto.AdminDTO;
import com.bank.integra.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AdminUpdateUserServiceTest {

    @InjectMocks
    private AdminUpdateUserService adminUpdateUserService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Test
    void updateUserFromForm_validInputWithNewPasswordAndBalance_shouldUpdateAllFields() {
        // Arrange
        Integer userId = 1;
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(userId);
        adminDTO.setFirstName("Updated John");
        adminDTO.setLastName("Updated Doe");
        adminDTO.setEmail("updated.john@example.com");
        adminDTO.setPassword("newPassword");
        adminDTO.setBalance(100.0);

        User existingUser = new User(userId, "oldPassword", true);
        UserDetails existingUserDetails = new UserDetails();
        existingUserDetails.setUserId(userId);
        existingUserDetails.setBalance(50.0);
        existingUser.setUserDetails(existingUserDetails);

        when(userService.getUserById(userId)).thenReturn(existingUser);
        when(userService.getUserDetailsByUserId(userId)).thenReturn(existingUserDetails);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        // Act
        adminUpdateUserService.updateUserFromForm(userId, adminDTO, bindingResult, redirectAttributes);

        // Assert
        verify(userService, times(1)).getUserById(userId);
        verify(userService, times(1)).getUserDetailsByUserId(userId);
        verify(passwordEncoder, times(1)).encode("newPassword");
        assertEquals("encodedPassword", existingUser.getPassword());
        assertEquals(100.0, existingUser.getUserDetails().getBalance());
        assertEquals("Updated John", existingUser.getUserDetails().getFirstName());
        assertEquals("Updated Doe", existingUser.getUserDetails().getLastName());
        assertEquals("updated.john@example.com", existingUser.getUserDetails().getEmail());
        verify(userService, times(1)).updateUser(existingUser);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("update"), eq(true));
    }

    @Test
    void updateUserFromForm_validInputWithNewPasswordOnly_shouldUpdatePasswordAndBasicDetails() {
        // Arrange
        Integer userId = 1;
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(userId);
        adminDTO.setFirstName("Updated John");
        adminDTO.setLastName("Updated Doe");
        adminDTO.setEmail("updated.john@example.com");
        adminDTO.setPassword("newPassword");

        User existingUser = new User(userId, "oldPassword", true);
        UserDetails existingUserDetails = new UserDetails();
        existingUserDetails.setUserId(userId);
        existingUserDetails.setBalance(50.0);
        existingUser.setUserDetails(existingUserDetails);

        when(userService.getUserById(userId)).thenReturn(existingUser);
        when(userService.getUserDetailsByUserId(userId)).thenReturn(existingUserDetails);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        // Act
        adminUpdateUserService.updateUserFromForm(userId, adminDTO, bindingResult, redirectAttributes);

        // Assert
        verify(userService, times(1)).getUserById(userId);
        verify(userService, times(1)).getUserDetailsByUserId(userId);
        verify(passwordEncoder, times(1)).encode("newPassword");
        assertEquals("encodedPassword", existingUser.getPassword());
        assertEquals(50.0, existingUser.getUserDetails().getBalance()); // Баланс залишився попереднім
        assertEquals("Updated John", existingUser.getUserDetails().getFirstName());
        assertEquals("Updated Doe", existingUser.getUserDetails().getLastName());
        assertEquals("updated.john@example.com", existingUser.getUserDetails().getEmail());
        verify(userService, times(1)).updateUser(existingUser);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("update"), eq(true));
    }

    @Test
    void updateUserFromForm_nonExistingUser_shouldThrowRuntimeException() {
        // Arrange
        Integer userId = 1;
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(userId);

        when(userService.getUserById(userId)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                adminUpdateUserService.updateUserFromForm(userId, adminDTO, bindingResult, redirectAttributes));
        verify(userService, times(1)).getUserById(userId);
        verify(userService, never()).getUserDetailsByUserId(anyInt());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userService, never()).updateUser(any());
        verify(redirectAttributes, never()).addFlashAttribute(anyString(), any());
    }
}
