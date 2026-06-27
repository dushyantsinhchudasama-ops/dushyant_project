package com.fooddelivery.repository;

import com.fooddelivery.enums.Role;
import com.fooddelivery.model.AbstractUser;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<AbstractUser> findAll();
    Optional<AbstractUser> findById(String id);
    Optional<AbstractUser> findByEmail(String email);
    List<AbstractUser> findByRole(Role role);
    void save(AbstractUser user);
    void update(AbstractUser user);
    void deleteById(String id);
    boolean existsByEmail(String email);
}
