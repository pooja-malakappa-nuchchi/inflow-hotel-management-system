package com.hms.controller;

import com.hms.model.InventoryItem;
import com.hms.model.InventoryLog;
import com.hms.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Manages hotel inventory and supplies.
 * Handles stock tracking, low-stock alerts, and inventory logs.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    /**
     * Retrieves all inventory items.
     */
    @GetMapping("/items")
    public List<InventoryItem> getAllItems() {
        // Fetch all items in inventory
        return inventoryService.getAllItems();
    }

    /**
     * Gets items with stock below minimum threshold.
     */
    @GetMapping("/low-stock")
    public List<InventoryItem> getLowStockItems() {
        // Find items that need reordering
        return inventoryService.getLowStockItems();
    }

    /**
     * Adds a new inventory item.
     */
    @PostMapping("/items")
    public ResponseEntity<?> addItem(@RequestBody InventoryItem item) {
        try {
            // Create new inventory item
            InventoryItem created = inventoryService.addItem(item);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updates stock quantity for an item.
     */
    @PostMapping("/update-stock")
    public ResponseEntity<?> updateStock(@RequestBody Map<String, Object> payload) {
        try {
            // Extract stock update details from request
            Long itemId = Long.valueOf(payload.get("itemId").toString());
            int quantityChange = Integer.parseInt(payload.get("quantityChange").toString());
            InventoryLog.LogType type = InventoryLog.LogType.valueOf(payload.get("type").toString());
            String reason = (String) payload.get("reason");

            // Update stock and create log entry
            InventoryItem updated = inventoryService.updateStock(itemId, quantityChange, type, reason);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves all inventory transaction logs.
     */
    @GetMapping("/logs")
    public List<InventoryLog> getAllLogs() {
        // Fetch complete history of stock changes
        return inventoryService.getAllLogs();
    }

    /**
     * Gets a specific inventory item by ID.
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        try {
            // Find and return item details
            InventoryItem item = inventoryService.getItemById(id);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            // Return 404 if item not found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates inventory item details.
     */
    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody InventoryItem item) {
        try {
            // Update item information (name, category, threshold, etc.)
            InventoryItem updated = inventoryService.updateItem(id, item);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Deletes an inventory item.
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        try {
            // Remove item from inventory
            inventoryService.deleteItem(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}