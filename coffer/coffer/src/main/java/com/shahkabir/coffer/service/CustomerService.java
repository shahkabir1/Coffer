package com.shahkabir.coffer.service;

import com.shahkabir.coffer.model.Customer;
import com.shahkabir.coffer.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(
            CustomerRepository customerRepository
    ) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer createCustomer(String firstName,
                                   String lastName, String email) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer first name cannot be empty"
            );
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer last name cannot be empty"
            );
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer email cannot be empty"
            );
        }

        Customer customer = new Customer(firstName, lastName, email);

        return customerRepository.save(customer);
    }

    @Transactional
    public Customer getCustomer(UUID customerId) {
        return customerRepository.findById(productId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Customer not found"
                        )
                );
    }
}
