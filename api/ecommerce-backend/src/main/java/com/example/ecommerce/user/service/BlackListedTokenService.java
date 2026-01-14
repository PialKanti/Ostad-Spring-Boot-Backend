package com.example.ecommerce.user.service;

import java.util.Date;

/**
 * Service interface for managing blacklisted JWT tokens.
 *
 * <p>Provides methods to blacklist tokens during logout and
 * check if a token has been revoked.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface BlackListedTokenService {

    /**
     * Adds a token to the blacklist with automatic expiry.
     * Token is removed from cache after its original expiration time.
     *
     * @param token the JWT token to blacklist
     * @param expirationDate the token's original expiration date
     */
    void markAsBlacklisted(String token, Date expirationDate);

    /**
     * Checks if a token is blacklisted.
     *
     * @param token the JWT token to check
     * @return true if token is blacklisted, false otherwise
     */
    boolean isBlacklisted(String token);
}
