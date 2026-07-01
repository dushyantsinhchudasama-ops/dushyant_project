package com.fooddelivery.repository;

import com.fooddelivery.enums.Role;
import com.fooddelivery.exception.DataAccessException;
import com.fooddelivery.factory.UserFactory;
import com.fooddelivery.model.AbstractUser;
import com.fooddelivery.model.Admin;
import com.fooddelivery.model.Customer;
import com.fooddelivery.model.DeliveryPerson;
import com.fooddelivery.utility.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileUserRepository implements UserRepository {
    private static final String DEFAULT_USERS_FILE = "data/users.txt";
    private static final String DELIMITER = "\u001F";
    private final List<AbstractUser> users = new ArrayList<>();
    private final String usersFile;

    public FileUserRepository() {
        this(DEFAULT_USERS_FILE);
    }

    public FileUserRepository(String usersFile) {
        this.usersFile = usersFile;
        loadUsers();
    }

    @Override
    public List<AbstractUser> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public Optional<AbstractUser> findById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<AbstractUser> findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public List<AbstractUser> findByRole(Role role) {
        return users.stream()
                .filter(user -> user.getRole() == role)
                .collect(Collectors.toList());
    }

    @Override
    public void save(AbstractUser user) {
        if (existsByEmail(user.getEmail())) {
            throw new DataAccessException("A user with email already exists: " + user.getEmail());
        }
        users.add(user);
        flush();
    }

    @Override
    public void update(AbstractUser user) {
        int index = findIndexById(user.getId());
        if (index == -1) {
            throw new DataAccessException("User not found for update: " + user.getId());
        }
        users.set(index, user);
        flush();
    }

    @Override
    public void deleteById(String id) {
        int index = findIndexById(id);
        if (index == -1) {
            throw new DataAccessException("User not found for delete: " + id);
        }
        users.remove(index);
        flush();
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    private void loadUsers() {
        List<String> lines = FileUtil.readAllLines(usersFile);
        users.clear();
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            AbstractUser user = parseLine(line);
            if (user != null) {
                users.add(user);
            }
        }
    }

    private AbstractUser parseLine(String line) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < 8) {
            throw new DataAccessException("Invalid user record: " + line);
        }

        String id = parts[0];
        Role role = Role.valueOf(parts[1]);
        String name = parts[2];
        String email = parts[3];
        String password = parts[4];
        String phoneNumber = parts.length > 5 ? parts[5] : "";

        String houseNo = "";
        String mainAddress = "";
        String pincode = "";
        String vehicleNumber = "";
        boolean available = false;

        if (role == Role.CUSTOMER) {
            if (parts.length >= 11) {
                houseNo = parts[6];
                mainAddress = parts[7];
                pincode = parts[8];
            } else if (parts.length >= 7) {
                houseNo = parts[6];
            }
        } else if (role == Role.DELIVERY_PERSON) {
            if (parts.length >= 11) {
                vehicleNumber = parts[9];
                available = Boolean.parseBoolean(parts[10]);
            } else if (parts.length >= 9) {
                vehicleNumber = parts[7];
                available = Boolean.parseBoolean(parts[8]);
            } else if (parts.length >= 8) {
                vehicleNumber = parts[6];
                available = Boolean.parseBoolean(parts[7]);
            }
        }

        return switch (role) {
            case SUPER_ADMIN -> UserFactory.createAdmin(id, name, email, password, true);
            case ADMIN -> UserFactory.createAdmin(id, name, email, password, false);
            case CUSTOMER -> UserFactory.createCustomer(id, name, email, password, phoneNumber, houseNo, mainAddress, pincode);
            case DELIVERY_PERSON -> {
                DeliveryPerson deliveryPerson = UserFactory.createDeliveryPerson(id, name, email, password, phoneNumber, vehicleNumber);
                deliveryPerson.setAvailable(available);
                yield deliveryPerson;
            }
        };
    }

    private int findIndexById(String id) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void flush() {
        List<String> lines = new ArrayList<>();
        for (AbstractUser user : users) {
            lines.add(formatUser(user));
        }
        FileUtil.writeAllLines(usersFile, lines);
    }

    private String formatUser(AbstractUser user) {
        String phoneNumber = "";
        String houseNo = "";
        String mainAddress = "";
        String pincode = "";
        String vehicleNumber = "";
        String available = "false";

        if (user instanceof Customer customer) {
            phoneNumber = customer.getPhoneNumber();
            houseNo = customer.getHouseNo();
            mainAddress = customer.getMainAddress();
            pincode = customer.getPincode();
        } else if (user instanceof DeliveryPerson deliveryPerson) {
            phoneNumber = deliveryPerson.getPhoneNumber();
            vehicleNumber = deliveryPerson.getVehicleNumber();
            available = String.valueOf(deliveryPerson.isAvailable());
        }

        return String.join(DELIMITER,
                user.getId(),
                user.getRole().name(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                phoneNumber,
                houseNo,
                mainAddress,
                pincode,
                vehicleNumber,
                available
        );
    }
}
