package com.hms.repository;

import com.hms.model.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for managing inventory transaction logs.
 * Provides database access for inventory history and audit trails.
 */
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    
    /**
     * Retrieves all inventory logs ordered by most recent first.
     * Used for viewing complete transaction history.
     */
    List<InventoryLog> findAllByOrderByTimestampDesc();
}