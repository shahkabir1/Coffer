package com.shahkabir.coffer.controller;


import com.shahkabir.coffer.dto.CreateCustomerRequest;
import com.shahkabir.coffer.dto.CustomerResponse;
import com.shahkabir.coffer.model.Customer;
import com.shahkabir.coffer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@PathVariable UUID id) {
        Customer customer = customerService.getCustomer(id);

        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCreationTime()
        );
    }

    @GetMapping
    public List<CustomerResponse> getAllCustomers(){
        return customerService.getAllCustomers()
                .stream()
                .map(customer -> new CustomerResponse(
                        customer.getId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail(),
                        customer.getCreationTime()
                ))
                .toList();
    }

    @PostMapping
    public CustomerResponse createCustomer(
            @RequestBody @Valid CreateCustomerRequest request) {
        Customer customer = customerService.createCustomer(
                request.firstName(),
                request.lastName(),
                request.email()
        );

        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCreationTime()

        );

    }
}
