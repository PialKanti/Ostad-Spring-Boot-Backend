package com.example.ecommerce.user.service;

import com.example.ecommerce.user.dto.request.ProfileUpdateRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Service interface for user management extending Spring Security's UserDetailsService.
 *
 * <p>Provides user loading for authentication and profile
 * management operations.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface UserService extends UserDetailsService {

    /**
     * Updates user profile information.
     *
     * @param username the username of the user to update
     * @param request the profile update details
     * @throws jakarta.persistence.EntityNotFoundException if user not found
     */
    void updateProfile(String username, ProfileUpdateRequest request);
}
