package com.example.ecommerce.user.service;

import com.example.ecommerce.user.dto.RefreshTokenData;
import com.example.ecommerce.user.entity.RefreshToken;
import com.example.ecommerce.user.entity.User;

import java.util.Optional;

/**
 * Service interface for refresh token management with secure storage.
 *
 * <p>Provides methods to create, find, and rotate refresh tokens
 * with hashed storage for security.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface RefreshTokenService {

    /**
     * Creates a new refresh token for the user.
     * Token is hashed before storage for security.
     *
     * @param user the user to create token for
     * @return token data containing raw token and expiry
     */
    RefreshTokenData create(User user);

    /**
     * Finds a refresh token by its raw value.
     * Hashes the raw token before lookup.
     *
     * @param rawToken the unhashed token value
     * @return optional containing the token if found
     */
    Optional<RefreshToken> findByToken(String rawToken);

    /**
     * Rotates refresh token by revoking the old one and issuing a new one.
     *
     * @param oldToken the token to revoke
     * @return new token data for the same user
     */
    RefreshTokenData rotate(RefreshToken oldToken);
}
