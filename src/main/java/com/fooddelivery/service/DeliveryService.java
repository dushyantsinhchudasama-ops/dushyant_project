package com.fooddelivery.service;

import com.fooddelivery.enums.Role;
import com.fooddelivery.exception.UserAlreadyExistsException;
import com.fooddelivery.factory.UserFactory;
import com.fooddelivery.model.AbstractUser;
import com.fooddelivery.model.DeliveryPerson;
import com.fooddelivery.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DeliveryService {
    private final UserRepository userRepository;

    public DeliveryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public DeliveryPerson addDeliveryPerson(String name, String email, String password, String phoneNumber, String vehicleNumber) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already registered: " + email);
        }
        String id = UUID.randomUUID().toString();
        DeliveryPerson deliveryPerson = UserFactory.createDeliveryPerson(id, name, email, password, phoneNumber, vehicleNumber);
        userRepository.save(deliveryPerson);
        return deliveryPerson;
    }

    public DeliveryPerson updateDeliveryPerson(String deliveryPersonId, String name, String email, String phoneNumber, String vehicleNumber, boolean available) {
        AbstractUser found = userRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery person not found: " + deliveryPersonId));
        if (!(found instanceof DeliveryPerson deliveryPerson)) {
            throw new IllegalArgumentException("User is not a delivery person: " + deliveryPersonId);
        }

        if (!deliveryPerson.getEmail().equalsIgnoreCase(email) && userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already registered: " + email);
        }

        deliveryPerson.setName(name);
        deliveryPerson.setEmail(email);
        deliveryPerson.setPhoneNumber(phoneNumber);
        deliveryPerson.setVehicleNumber(vehicleNumber);
        deliveryPerson.setAvailable(available);
        userRepository.update(deliveryPerson);
        return deliveryPerson;
    }

    public void removeDeliveryPerson(String deliveryPersonId) {
        AbstractUser found = userRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery person not found: " + deliveryPersonId));
        if (!(found instanceof DeliveryPerson)) {
            throw new IllegalArgumentException("User is not a delivery person: " + deliveryPersonId);
        }
        userRepository.deleteById(deliveryPersonId);
    }

    public DeliveryPerson getDeliveryPersonById(String deliveryPersonId) {
        AbstractUser found = userRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery person not found: " + deliveryPersonId));
        if (!(found instanceof DeliveryPerson deliveryPerson)) {
            throw new IllegalArgumentException("User is not a delivery person: " + deliveryPersonId);
        }
        return deliveryPerson;
    }

    public List<DeliveryPerson> getAllDeliveryPersons() {
        return userRepository.findByRole(Role.DELIVERY_PERSON).stream()
                .map(user -> (DeliveryPerson) user)
                .collect(Collectors.toList());
    }

    public DeliveryPerson findAvailableDeliveryPerson() {
        return getAllDeliveryPersons().stream()
                .filter(DeliveryPerson::isAvailable)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available delivery person found."));
    }
}
