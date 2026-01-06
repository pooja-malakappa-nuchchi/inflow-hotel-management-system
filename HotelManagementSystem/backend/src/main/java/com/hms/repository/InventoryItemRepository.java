package com.hms.repository;

import com.hms.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for managing inventory items.
 * Provides database queries for stock tracking and low-stock alerts.
 */
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    /**
     * Finds items with stock at or below the reorder threshold.
     * Used to identify items that need restocking.
     */
    List<InventoryItem> findByQuantityLessThanEqual(Integer reorderLevel);
}