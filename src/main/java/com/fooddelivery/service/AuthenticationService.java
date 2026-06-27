package com.fooddelivery.service;

import com.fooddelivery.exception.InvalidCredentialsException;
import com.fooddelivery.model.AbstractUser;
import com.fooddelivery.repository.UserRepository;

import java.util.Optional;

public class AuthenticationService {
    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AbstractUser login(String email, String password) {
        Optional<AbstractUser> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        AbstractUser user = optionalUser.get();
        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        return user;
    }

    public void changePassword(String userId, String currentPassword, String newPassword) {
        AbstractUser user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found."));

        if (!user.getPassword().equals(currentPassword)) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }

        user.setPassword(newPassword);
        userRepository.update(user);
    }

    public void logout(AbstractUser user) {
        // Logout is handled by the application session layer. This method exists for future extension.
    }
}
