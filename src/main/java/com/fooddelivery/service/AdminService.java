package com.fooddelivery.service;

import com.fooddelivery.enums.Role;
import com.fooddelivery.exception.AlreadyExistsException;
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
        return createAdmin(null, name, email, password, false);
    }

    public Admin createAdmin(String actorId, String name, String email, String password) {
        return createAdmin(actorId, name, email, password, false);
    }

    public Admin createAdmin(String actorId, String name, String email, String password, boolean superAdmin) {
        ensureAuthorizedToManageAdmins(actorId);
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExistsException("Email already registered: " + email);
        }
        String id = generateNextUserId();
        Admin admin = UserFactory.createAdmin(id, name, email, password, superAdmin);
        userRepository.save(admin);
        return admin;
    }

    public List<Admin> getAllAdmins() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN)
                .map(Admin.class::cast)
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

    public void removeAdmin(String actorId, String adminId) {
        ensureSuperAdmin(actorId);
        AbstractUser target = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found: " + adminId));
        if (!(target instanceof Admin)) {
            throw new IllegalArgumentException("User is not an admin: " + adminId);
        }
        if (adminId.equals(actorId)) {
            throw new IllegalStateException("Super admin cannot remove their own account.");
        }
        userRepository.deleteById(adminId);
    }

    public void initializeDefaultAdmin(String name, String email, String password) {
        AbstractUser existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser == null) {
            createAdmin(null, name, email, password, true);
            return;
        }
        if (existingUser instanceof Admin admin && !isNumericId(admin.getId())) {
            String newId = generateNextUserId();
            Admin migratedAdmin = new Admin(newId, admin.getName(), admin.getEmail(), admin.getPassword(), admin.getRole() == Role.SUPER_ADMIN);
            userRepository.deleteById(admin.getId());
            userRepository.save(migratedAdmin);
        }
    }

    private void ensureAuthorizedToManageAdmins(String actorId) {
        if (actorId == null || actorId.isBlank()) {
            return;
        }
        AbstractUser actor = userRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found: " + actorId));
        if (actor.getRole() != Role.ADMIN && actor.getRole() != Role.SUPER_ADMIN) {
            throw new IllegalStateException("Only admins can manage admin accounts.");
        }
    }

    private void ensureSuperAdmin(String actorId) {
        AbstractUser actor = userRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found: " + actorId));
        if (actor.getRole() != Role.SUPER_ADMIN) {
            throw new IllegalStateException("Only a super admin can remove admin accounts.");
        }
    }

    private String generateNextUserId() {
        int maxId = userRepository.findAll().stream()
                .map(AbstractUser::getId)
                .mapToInt(this::parseNumericId)
                .max()
                .orElse(0);
        return String.valueOf(maxId + 1);
    }

    private boolean isNumericId(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        try {
            Integer.parseInt(id);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
