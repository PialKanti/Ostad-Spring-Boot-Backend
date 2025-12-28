package com.example.ecommerce.common.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {
    String generateToken(UserDetails userDetails);

    Date extractExpiration(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
}
