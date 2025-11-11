//package com.bank.integra.tests;
//
//import com.bank.integra.user.repository.UserDetailsRepository;
//import com.bank.integra.user.model.UserDetails;
//import com.bank.integra.user.transaction.service.PaymentService;
//import com.bank.integra.user.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.ui.Model;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//public class PaymentTest {
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private UserDetailsRepository userDetailsRepository;
//
//    @Mock
//    private Model model;
//
//    @InjectMocks
//    private PaymentService paymentService;
//
//    @Test
//    void makePayment_sufficientFunds_shouldUpdateBalancesAndSave() {
//        // Arrange
//        Integer payerId = 1;
//        Integer receiverId = 2;
//        Double amount = 50.0;
//
//        UserDetails payerDetails = new UserDetails();
//        payerDetails.setBalance(100.0);
//
//        UserDetails receiverDetails = new UserDetails();
//        receiverDetails.setBalance(20.0);
//
//        when(userService.getUserDetailsByUserId(payerId)).thenReturn(payerDetails);
//        when(userService.getUserDetailsByUserId(receiverId)).thenReturn(receiverDetails);
//
//        // Act
//        paymentService.makePayment(payerId, receiverId, amount, model);
//
//        // Assert
//        assertEquals(50.0, payerDetails.getBalance());
//        assertEquals(70.0, receiverDetails.getBalance());
//        verify(userDetailsRepository, times(1)).save(payerDetails);
//        verify(userDetailsRepository, times(1)).save(receiverDetails);
//        verify(model, never()).addAttribute(eq("paymentError"), anyString());
//    }
//
//    @Test
//    void makePayment_insufficientFunds_shouldNotUpdateBalancesAndAddErrorToModel() {
//        // Arrange
//        Integer payerId = 1;
//        Integer receiverId = 2;
//        Double amount = 150.0;
//
//        UserDetails payerDetails = new UserDetails();
//        payerDetails.setBalance(100.0);
//
//        UserDetails receiverDetails = new UserDetails();
//        receiverDetails.setBalance(20.0);
//
//        when(userService.getUserDetailsByUserId(payerId)).thenReturn(payerDetails);
//        when(userService.getUserDetailsByUserId(receiverId)).thenReturn(receiverDetails);
//
//        // Act
//        paymentService.makePayment(payerId, receiverId, amount, model);
//
//        // Assert
//        assertEquals(100.0, payerDetails.getBalance());
//        assertEquals(20.0, receiverDetails.getBalance());
//        verify(userDetailsRepository, never()).save(any(UserDetails.class));
//        verify(model, times(1)).addAttribute(eq("paymentErrorNotEnoughFunds"), eq("Not enough funds for transfer operation."));
//    }
//
//    @Test
//    void makePayment_payerNotFound_shouldNotUpdateBalancesAndPotentiallyHandleException() {
//        // Arrange
//        Integer payerId = 1;
//        Integer receiverId = 2;
//        Double amount = 50.0;
//
//        UserDetails receiverDetails = new UserDetails();
//        receiverDetails.setBalance(20.0);
//
//        when(userService.getUserDetailsByUserId(payerId)).thenReturn(null);
//        when(userService.getUserDetailsByUserId(receiverId)).thenReturn(receiverDetails);
//
//        // Act
//        paymentService.makePayment(payerId, receiverId, amount, model);
//
//        // Assert
//        verify(userDetailsRepository, never()).save(any(UserDetails.class));
//        verify(model, times(1)).addAttribute(eq("paymentErrorInvalidPayerId"), eq("The user id is invalid. Please, try again."));
//        // You might want to assert on a specific exception being thrown or a different error handling mechanism
//    }
//}
