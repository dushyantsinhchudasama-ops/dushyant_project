package com.fooddelivery.service;

import com.fooddelivery.enums.Role;
import com.fooddelivery.exception.UserAlreadyExistsException;
import com.fooddelivery.factory.UserFactory;
import com.fooddelivery.model.AbstractUser;
import com.fooddelivery.model.Customer;
import com.fooddelivery.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerService {
    private final UserRepository userRepository;

    public CustomerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Customer registerCustomer(String name, String email, String password, String phoneNumber, String address) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already registered: " + email);
        }
        String id = generateNextUserId();
        Customer customer = UserFactory.createCustomer(id, name, email, password, phoneNumber, address);
        userRepository.save(customer);
        return customer;
    }

    public Customer updateProfile(String customerId, String name, String email, String phoneNumber, String address) {
        AbstractUser found = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        if (!(found instanceof Customer customer)) {
            throw new IllegalArgumentException("User is not a customer: " + customerId);
        }

        if (!customer.getEmail().equalsIgnoreCase(email) && userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already registered: " + email);
        }

        customer.setName(name);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);
        userRepository.update(customer);
        return customer;
    }

    public Customer getCustomerById(String customerId) {
        AbstractUser found = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        if (!(found instanceof Customer customer)) {
            throw new IllegalArgumentException("User is not a customer: " + customerId);
        }
        return customer;
    }

    public List<Customer> getAllCustomers() {
        return userRepository.findByRole(Role.CUSTOMER).stream()
                .map(user -> (Customer) user)
                .collect(Collectors.toList());
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
