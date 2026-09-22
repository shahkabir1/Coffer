package com.shahkabir.coffer.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    private Instant createdAt;

    public Customer(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Instant getCreationTime() {
        return createdAt;
    }

    public void changeFirstName(String newFirstName) {
        this.firstName = newFirstName;
    }

    public void changeLastName(String newLastName) {
        this.lastName = newLastName;
    }

    public void changeEmail(String newEmail) {
        this.email = newEmail;
    }
}
