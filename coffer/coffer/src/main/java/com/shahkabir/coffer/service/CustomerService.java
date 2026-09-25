package com.shahkabir.coffer.service;

import com.shahkabir.coffer.exception.CustomerNotFoundException;
import com.shahkabir.coffer.model.Customer;
import com.shahkabir.coffer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public Customer updateCustomer(UUID customerId, UpdateCustomerRequest request) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() ->
                        new NoSuchElementException("Customer not found")
                );
        if (request.)
        }
        return customer;
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer getCustomer(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found: " + customerId
                        )
                );
    }
}
