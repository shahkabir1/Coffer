package com.shahkabir.coffer.model;

import com.shahkabir.coffer.model.enums.Role;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    protected User() {}

    public User(String email, String passwordHash, Role role, Customer customer){
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.customer = customer;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail(){
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public Customer getCustomer() {
        return customer;
    }
}
