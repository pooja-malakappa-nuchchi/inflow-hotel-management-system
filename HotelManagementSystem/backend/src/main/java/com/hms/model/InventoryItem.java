package com.hms.model;

import jakarta.persistence.*;

/**
 * Represents an inventory item in the hotel.
 * Tracks supplies, equipment, and their stock levels.
 */
@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // Item name (e.g., "Towels", "Soap", "Light Bulbs")

    @Column(nullable = false)
    private Integer quantity;  // Current stock quantity

    @Column(nullable = false)
    private Integer reorderLevel;  // Minimum quantity before reordering needed

    private Double price;  // Unit price of the item

    // Default constructor
    public InventoryItem() {
    }

    // Constructor for creating new items
    public InventoryItem(String name, Integer quantity, Integer reorderLevel, Double price) {
        this.name = name;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.price = price;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}