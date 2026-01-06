package com.hms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a system user (staff member).
 * Handles authentication and role-based access control.
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // Full name of the user

    @Column(unique = true, nullable = false)
    private String email;  // Login email (must be unique)

    @Column(nullable = false)
    private String password;  // Encrypted password

    @Enumerated(EnumType.STRING)
    private Role role;  // User's role/permission level

    private LocalDateTime createdAt;  // When account was created

    /**
     * Automatically sets creation timestamp.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * User roles with different permission levels.
     */
    public enum Role {
        ADMIN,         // Full system access
        STAFF,         // General staff (legacy role)
        MANAGER,       // Manager permissions
        RECEPTIONIST,  // Front desk operations
        HOUSEKEEPING   // Housekeeping staff
    }

    // Default constructor
    public User() {
    }

    // Full constructor
    public User(Long id, String name, String email, String password, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Compare users by ID and email
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}