package com.fooddelivery.repository;

import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.exception.DataAccessException;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.utility.FileUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileOrderRepository implements OrderRepository {
    private static final String DEFAULT_ORDERS_FILE = "data/orders.txt";
    private static final String DEFAULT_ORDER_DETAILS_FILE = "data/orderDetails.txt";
    private static final String DELIMITER = "\u001F";

    private final List<Order> orders = new ArrayList<>();
    private final String ordersFile;
    private final String orderDetailsFile;

    public FileOrderRepository() {
        this(DEFAULT_ORDERS_FILE, DEFAULT_ORDER_DETAILS_FILE);
    }

    public FileOrderRepository(String ordersFile, String orderDetailsFile) {
        this.ordersFile = ordersFile;
        this.orderDetailsFile = orderDetailsFile;
        loadOrders();
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders);
    }

    @Override
    public Optional<Order> findById(String id) {
        return orders.stream()
                .filter(order -> order.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        return orders.stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByDeliveryPersonId(String deliveryPersonId) {
        return orders.stream()
                .filter(order -> order.getDeliveryPersonId() != null && order.getDeliveryPersonId().equals(deliveryPersonId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orders.stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Order order) {
        if (findById(order.getId()).isPresent()) {
            throw new DataAccessException("Order already exists: " + order.getId());
        }
        orders.add(order);
        flush();
    }

    @Override
    public void update(Order order) {
        int index = findIndexById(order.getId());
        if (index == -1) {
            throw new DataAccessException("Order not found: " + order.getId());
        }
        orders.set(index, order);
        flush();
    }

    @Override
    public void deleteById(String id) {
        int index = findIndexById(id);
        if (index == -1) {
            throw new DataAccessException("Order not found: " + id);
        }
        orders.remove(index);
        flush();
    }

    private void loadOrders() {
        List<String> orderLines = FileUtil.readAllLines(ordersFile);
        Map<String, List<OrderItem>> orderItems = loadOrderItems();
        orders.clear();
        for (String line : orderLines) {
            if (line.isBlank()) {
                continue;
            }
            Order order = parseOrderLine(line, orderItems.getOrDefault(parseOrderId(line), List.of()));
            orders.add(order);
        }
    }

    private String parseOrderId(String line) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < 1) {
            throw new DataAccessException("Invalid order record: " + line);
        }
        return parts[0];
    }

    private Map<String, List<OrderItem>> loadOrderItems() {
        List<String> detailLines = FileUtil.readAllLines(orderDetailsFile);
        Map<String, List<OrderItem>> map = new HashMap<>();
        for (String line : detailLines) {
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split(DELIMITER, -1);
            if (parts.length != 6) {
                throw new DataAccessException("Invalid order detail record: " + line);
            }
            String orderId = parts[0];
            int itemId;
            try {
                itemId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                throw new DataAccessException("Invalid order item id format: " + line, e);
            }
            String itemName = parts[2];
            int quantity;
            double unitPrice;
            double subtotal;
            try {
                quantity = Integer.parseInt(parts[3]);
                unitPrice = Double.parseDouble(parts[4]);
                subtotal = Double.parseDouble(parts[5]);
            } catch (NumberFormatException e) {
                throw new DataAccessException("Invalid order detail number format: " + line, e);
            }
            OrderItem orderItem = new OrderItem(itemId, itemName, quantity, unitPrice);
            map.computeIfAbsent(orderId, id -> new ArrayList<>()).add(orderItem);
        }
        return map;
    }

    private Order parseOrderLine(String line, List<OrderItem> items) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length != 10 && parts.length != 11) {
            throw new DataAccessException("Invalid order record: " + line);
        }

        String orderId = parts[0];
        String customerId = parts[1];
        String deliveryPersonId = parts[2].isBlank() ? null : parts[2];
        double originalAmount;
        double discountPercentage;
        double discountAmount;
        double finalAmount;
        try {
            originalAmount = Double.parseDouble(parts[3]);
            discountPercentage = Double.parseDouble(parts[4]);
            discountAmount = Double.parseDouble(parts[5]);
            finalAmount = Double.parseDouble(parts[6]);
        } catch (NumberFormatException e) {
            throw new DataAccessException("Invalid order number format: " + line, e);
        }

        String paymentType = parts[7];
        String houseNo = "";
        String mainAddress = "";
        String pincode = "";
        int statusIndex = 8;
        int dateIndex = 9;

        if (parts.length == 11) {
            deliveryAddress = parts[8];
            statusIndex = 9;
            dateIndex = 10;
        }

        OrderStatus status;
        try {
            status = OrderStatus.valueOf(parts[statusIndex]);
        } catch (IllegalArgumentException e) {
            throw new DataAccessException("Invalid order status: " + line, e);
        }

        LocalDateTime orderDate;
        try {
            orderDate = LocalDateTime.parse(parts[dateIndex]);
        } catch (DateTimeParseException e) {
            throw new DataAccessException("Invalid order date format: " + line, e);
        }

        return new Order(
                orderId,
                customerId,
                deliveryPersonId,
                items,
                originalAmount,
                discountPercentage,
                discountAmount,
                finalAmount,
                com.fooddelivery.enums.PaymentType.valueOf(paymentType),
                houseNo,
                mainAddress,
                pincode,
                status,
                orderDate);
    }

    private int findIndexById(String id) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void flush() {
        List<String> orderLines = new ArrayList<>();
        List<String> detailLines = new ArrayList<>();
        for (Order order : orders) {
            orderLines.add(formatOrder(order));
            for (OrderItem item : order.getItems()) {
                detailLines.add(formatOrderItem(order.getId(), item));
            }
        }
        FileUtil.writeAllLines(ordersFile, orderLines);
        FileUtil.writeAllLines(orderDetailsFile, detailLines);
    }

    private String formatOrder(Order order) {
        return String.join(DELIMITER,
                order.getId(),
                order.getCustomerId(),
                order.getDeliveryPersonId() == null ? "" : order.getDeliveryPersonId(),
                String.valueOf(order.getOriginalAmount()),
                String.valueOf(order.getDiscountPercentage()),
                String.valueOf(order.getDiscountAmount()),
                String.valueOf(order.getFinalAmount()),
                order.getPaymentType().name(),
                order.getHouseNO() == null ? "" : order.getHouseNO(),
                order.getMainAddress() == null ? "" : order.getMainAddress(),
                order.getPincode() == null ? "" : order.getPincode(),
                order.getStatus().name(),
                order.getOrderDate().toString()
        );
    }

    private String formatOrderItem(String orderId, OrderItem item) {
        return String.join(DELIMITER,
                orderId,
                String.valueOf(item.getItemId()),
                item.getItemName(),
                String.valueOf(item.getQuantity()),
                String.valueOf(item.getUnitPrice()),
                String.valueOf(item.getSubtotal())
        );
    }
}
