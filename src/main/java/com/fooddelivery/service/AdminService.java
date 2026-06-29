package com.fooddelivery.service;

import com.fooddelivery.exception.UserAlreadyExistsException;
import com.fooddelivery.factory.UserFactory;
import com.fooddelivery.model.Admin;
import com.fooddelivery.model.AbstractUser;
import com.fooddelivery.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Admin createAdmin(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already registered: " + email);
        }
        String id = generateNextUserId();
        Admin admin = UserFactory.createAdmin(id, name, email, password);
        userRepository.save(admin);
        return admin;
    }

    public List<Admin> getAllAdmins() {
        return userRepository.findByRole(com.fooddelivery.enums.Role.ADMIN).stream()
                .map(user -> (Admin) user)
                .collect(Collectors.toList());
    }

    public Admin getAdminById(String adminId) {
        AbstractUser user = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found: " + adminId));
        if (!(user instanceof Admin)) {
            throw new IllegalArgumentException("User is not an admin: " + adminId);
        }
        return (Admin) user;
    }

    public void initializeDefaultAdmin(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }
        createAdmin(name, email, password);
    }

    private String generateNextUserId() {
        int maxId = userRepository.findAll().stream()
                .map(AbstractUser::getId)
                .mapToInt(this::parseNumericId)
                .max()
                .orElse(0);
        return String.valueOf(maxId + 1);
    }

    private int parseNumericId(String id) {
        if (id == null || id.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
