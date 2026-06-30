package com.fooddelivery.test;

import com.fooddelivery.enums.Role;
import com.fooddelivery.exception.InvalidCredentialsException;
import com.fooddelivery.exception.AlreadyExistsException;
import com.fooddelivery.model.Admin;
import com.fooddelivery.model.Customer;
import com.fooddelivery.model.DeliveryPerson;
import com.fooddelivery.repository.FileUserRepository;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.AdminService;
import com.fooddelivery.service.AuthenticationService;
import com.fooddelivery.service.CustomerService;
import com.fooddelivery.service.DeliveryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private static final Path TEST_FILE = Paths.get("data", "users-test.txt");
    private UserRepository userRepository;
    private AuthenticationService authenticationService;
    private AdminService adminService;
    private CustomerService customerService;
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() throws IOException {
        if (Files.exists(TEST_FILE)) {
            Files.delete(TEST_FILE);
        }
        Files.createDirectories(TEST_FILE.getParent());
        Files.createFile(TEST_FILE);
        userRepository = new FileUserRepository(TEST_FILE.toString());
        authenticationService = new AuthenticationService(userRepository);
        adminService = new AdminService(userRepository);
        customerService = new CustomerService(userRepository);
        deliveryService = new DeliveryService(userRepository);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(TEST_FILE)) {
            Files.delete(TEST_FILE);
        }
    }

    @Test
    void shouldRegisterCustomerAndAllowLogin() {
        Customer customer = customerService.registerCustomer("Mira Patel", "mira@example.com", "pass123", "9999999999", "12 Jain Street");
        assertNotNull(customer.getId());
        assertEquals(Role.CUSTOMER, customer.getRole());

        assertTrue(userRepository.existsByEmail("mira@example.com"));

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login("mira@example.com", "wrongpass"));

        assertEquals(customer.getId(), authenticationService.login("mira@example.com", "pass123").getId());
    }

    @Test
    void shouldCreateAdminAndGetAllAdmins() {
        Admin admin = adminService.createAdmin("Admin One", "admin1@example.com", "adminpass");
        assertEquals("Admin One", admin.getName());
        assertEquals(Role.ADMIN, admin.getRole());

        assertEquals(1, adminService.getAllAdmins().size());
        assertEquals(admin.getId(), adminService.getAllAdmins().get(0).getId());
    }

    @Test
    void shouldAllowRegularAdminToCreateAdminButOnlySuperAdminToRemoveAdmin() {
        Admin superAdmin = adminService.createAdmin(null, "Super Admin", "super@example.com", "superpass", true);
        Admin createdByAdmin = adminService.createAdmin(superAdmin.getId(), "Regular Admin", "admin@example.com", "adminpass");

        assertEquals(Role.ADMIN, createdByAdmin.getRole());
        assertEquals(2, adminService.getAllAdmins().size());

        assertThrows(IllegalStateException.class,
                () -> adminService.removeAdmin(createdByAdmin.getId(), superAdmin.getId()));

        adminService.removeAdmin(superAdmin.getId(), createdByAdmin.getId());
        assertEquals(1, adminService.getAllAdmins().size());
    }

    @Test
    void shouldConvertLegacyAdminIdsToNumericIds() {
        userRepository.save(new Admin("legacy-admin-id", "Legacy Admin", "admin@fooddelivery.com", "pass"));

        adminService.initializeDefaultAdmin("Super Admin", "admin@fooddelivery.com", "admin123");

        Admin migratedAdmin = (Admin) userRepository.findByEmail("admin@fooddelivery.com").orElseThrow();
        assertTrue(migratedAdmin.getId().chars().allMatch(Character::isDigit));
    }

    @Test
    void shouldNotAllowDuplicateEmailForCustomerRegistration() {
        customerService.registerCustomer("Sam", "sam@example.com", "pass", "9999999999", "34 Road");
        assertThrows(AlreadyExistsException.class,
                () -> customerService.registerCustomer("Sam Two", "sam@example.com", "pass2", "8888888888", "35 Road"));
    }

    @Test
    void shouldUpdateCustomerProfile() {
        Customer customer = customerService.registerCustomer("Nina", "nina@example.com", "pass", "7777777777", "101 Lane");
        Customer updated = customerService.updateProfile(customer.getId(), "Nina Roy", "nina.roy@example.com", "6666666666", "102 Lane");

        assertEquals("Nina Roy", updated.getName());
        assertEquals("nina.roy@example.com", updated.getEmail());
        assertEquals("6666666666", updated.getPhoneNumber());
    }

    @Test
    void shouldAddAndRemoveDeliveryPerson() {
        DeliveryPerson deliveryPerson = deliveryService.addDeliveryPerson("Ravi", "ravi@example.com", "deliver", "7777777777", "KA01AB1234");
        assertEquals(Role.DELIVERY_PERSON, deliveryPerson.getRole());
        assertEquals(1, deliveryService.getAllDeliveryPersons().size());

        deliveryService.removeDeliveryPerson(deliveryPerson.getId());
        assertEquals(0, deliveryService.getAllDeliveryPersons().size());
    }

    @Test
    void shouldFindAvailableDeliveryPerson() {
        DeliveryPerson deliveryPerson = deliveryService.addDeliveryPerson("Deepa", "deepa@example.com", "deliver", "7777777777", "DL05CD5678");
        DeliveryPerson found = deliveryService.findAvailableDeliveryPerson();
        assertEquals(deliveryPerson.getId(), found.getId());
    }
}
