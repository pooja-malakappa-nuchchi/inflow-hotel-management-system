package com.hms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Tracks user activities and system actions.
 * Records who did what and when for audit purposes.
 */
@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // The user who performed the action

    @Column(nullable = false)
    private String action;  // What action was performed (e.g., "Created booking", "Updated room")

    @Column(columnDefinition = "TEXT")
    private String details;  // Additional details about the action

    private LocalDateTime timestamp;  // When the action occurred

    /**
     * Automatically sets timestamp when log is created.
     */
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    // Default constructor
    public ActivityLog() {
    }

    // Constructor for quick log creation
    public ActivityLog(User user, String action, String details) {
        this.user = user;
        this.action = action;
        this.details = details;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}