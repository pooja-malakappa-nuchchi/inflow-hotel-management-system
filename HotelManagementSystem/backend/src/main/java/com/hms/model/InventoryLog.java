package com.hms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Tracks inventory changes and transactions.
 * Records who changed what, when, and why for audit purposes.
 */
@Entity
@Table(name = "inventory_logs")
public class InventoryLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;  // Which item was affected

    @Enumerated(EnumType.STRING)
    private LogType type;  // Type of transaction

    private Integer quantityChange;  // How much quantity changed 
    private String reason;  // Why the change was made
    private LocalDateTime timestamp;  // When it happened

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // Who made the change

    /**
     * Types of inventory transactions.
     */
    public enum LogType {
        RESTOCK,  // Adding new stock
        USAGE,    // Items used/consumed
        DAMAGE,   // Items damaged
        LOSS      // Items lost/stolen
    }

    /**
     * Automatically sets timestamp when log is created.
     */
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    // Default constructor
    public InventoryLog() {
    }

    // Constructor for quick log creation
    public InventoryLog(InventoryItem item, LogType type, Integer quantityChange, String reason, User user) {
        this.item = item;
        this.type = type;
        this.quantityChange = quantityChange;
        this.reason = reason;
        this.user = user;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InventoryItem getItem() {
        return item;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}