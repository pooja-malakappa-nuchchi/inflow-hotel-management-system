package com.hms.service;

import com.hms.model.InventoryItem;
import com.hms.model.InventoryLog;
import com.hms.model.User;
import com.hms.repository.InventoryItemRepository;
import com.hms.repository.InventoryLogRepository;
import com.hms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing hotel inventory and supplies.
 * Handles stock tracking, low-stock alerts, and transaction logging.
 */
@Service
public class InventoryService {

    @Autowired
    private InventoryItemRepository itemRepository;

    @Autowired
    private InventoryLogRepository logRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all inventory items.
     */
    public List<InventoryItem> getAllItems() {
        return itemRepository.findAll();
    }

    /**
     * Finds items with stock at or below reorder threshold.
     * Filters items where quantity <= reorderLevel for restocking alerts.
     */
    public List<InventoryItem> getLowStockItems() {
        // Filter items needing reorder in memory
        // (Custom query would be more efficient for large inventories)
        return itemRepository.findAll().stream()
                .filter(item -> item.getQuantity() <= item.getReorderLevel())
                .collect(Collectors.toList());
    }

    /**
     * Adds a new inventory item.
     */
    public InventoryItem addItem(InventoryItem item) {
        return itemRepository.save(item);
    }

    /**
     * Updates item stock quantity and logs the transaction.
     * Validates sufficient stock before allowing reductions.
     */
    public InventoryItem updateStock(Long itemId, int quantityChange, InventoryLog.LogType type, String reason) {
        // Find item
        InventoryItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Calculate new quantity
        int newQuantity = item.getQuantity() + quantityChange;
        
        // Prevent negative stock
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        
        // Update quantity
        item.setQuantity(newQuantity);
        InventoryItem savedItem = itemRepository.save(item);

        // Create audit log entry
        User user = getCurrentUser();
        InventoryLog log = new InventoryLog(savedItem, type, quantityChange, reason, user);
        logRepository.save(log);

        return savedItem;
    }

    /**
     * Retrieves all inventory transaction logs ordered by most recent.
     */
    public List<InventoryLog> getAllLogs() {
        return logRepository.findAllByOrderByTimestampDesc();
    }

    /**
     * Gets a specific inventory item by ID.
     */
    public InventoryItem getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    /**
     * Updates inventory item details (name, price, reorder level, etc.).
     */
    public InventoryItem updateItem(Long id, InventoryItem updatedItem) {
        // Find existing item
        InventoryItem existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        // Update item properties
        existingItem.setName(updatedItem.getName());
        existingItem.setQuantity(updatedItem.getQuantity());
        existingItem.setReorderLevel(updatedItem.getReorderLevel());
        existingItem.setPrice(updatedItem.getPrice());

        return itemRepository.save(existingItem);
    }

    /**
     * Deletes an inventory item.
     */
    public void deleteItem(Long id) {
        // Verify item exists before deleting
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

    /**
     * Gets the currently logged-in user from security context.
     * Returns null if user cannot be determined.
     */
    private User getCurrentUser() {
        try {
            // Get authenticated user from Spring Security
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email;
            
            // Extract email from principal
            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            } else {
                email = principal.toString();
            }
            
            // Find user in database
            return userRepository.findByEmail(email).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}