package com.example.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Inventory Service application.
 *
 * <p>This microservice handles stock management and reservations for the e-commerce platform.</p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Stock tracking (available and reserved quantities)</li>
 *   <li>Temporary stock reservations for pending orders</li>
 *   <li>Automatic expiry of abandoned reservations (runs every 5 minutes)</li>
 *   <li>Transaction logging for audit trail</li>
 * </ul>
 *
 * <p>{@code @EnableScheduling} activates Spring's scheduled task execution capability,
 * which is used by {@link com.example.inventory.scheduler.ReservationExpiryScheduler}
 * to automatically expire old reservations and revert stock.</p>
 */
@SpringBootApplication
@EnableScheduling
public class InventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}

}
