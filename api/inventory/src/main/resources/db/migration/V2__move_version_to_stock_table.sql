-- Migration: Move optimistic locking version column from stock_reservation to stock table
-- Reason: Stock entity is the critical entity for concurrency control, not the reservation.
--         Multiple concurrent operations modify stock quantities, so optimistic locking
--         should be on the Stock entity to prevent race conditions and data corruption.

-- Add version column to stock table for optimistic locking
-- This column is automatically incremented by JPA/Hibernate on each update
-- If two transactions try to update the same stock record simultaneously,
-- the second one will fail with OptimisticLockException, preventing data corruption
ALTER TABLE stock
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- Add comment explaining the version column purpose
COMMENT ON COLUMN stock.version IS 'Optimistic locking version - prevents concurrent stock modifications from corrupting quantity counts';

-- Remove version column from stock_reservation table
-- Reservations don't need optimistic locking because:
-- 1. Stock modifications are the critical section requiring locking
-- 2. Reservation status transitions are sequential (PENDING -> CONFIRMED/CANCELLED)
-- 3. The Stock entity's version handles the concurrency for quantity updates
ALTER TABLE stock_reservation
    DROP COLUMN version;
