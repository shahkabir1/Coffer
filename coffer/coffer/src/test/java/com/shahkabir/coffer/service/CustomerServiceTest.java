package com.shahkabir.coffer.service;

import com.shahkabir.coffer.exception.CustomerNotFoundException;
import com.shahkabir.coffer.model.Customer;
import com.shahkabir.coffer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getCustomer_returnsCustomerWhenItExists() {
        UUID customerId = UUID.randomUUID();
        Customer customer = mock(Customer.class);

        when (customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomer(customerId);

        assertSame(customer, result);
        verify(customerRepository).findById(customerId);
    }

    @Test
    void getCustomer_throwsWhenCustomerDoesNotExist() {
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomer(customerId)
        );

        verify(customerRepository).findById(customerId);
    }
}
