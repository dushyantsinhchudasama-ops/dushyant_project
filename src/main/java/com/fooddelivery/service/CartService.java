package com.fooddelivery.service;

import com.fooddelivery.model.Cart;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.repository.MenuRepository;

import java.util.HashMap;
import java.util.Map;

public class CartService {
    private final MenuRepository menuRepository;
    private final Map<String, Cart> carts = new HashMap<>();

    public CartService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public Cart getCart(String customerId) {
        return carts.computeIfAbsent(customerId, Cart::new);
    }

    public void addItemToCart(String customerId, int menuItemId, int quantity) {
        MenuItem menuItem = menuRepository.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + menuItemId));
        if (!menuItem.isAvailable()) {
            throw new IllegalArgumentException("Menu item is not available: " + menuItem.getName());
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        Cart cart = getCart(customerId);
        cart.addItem(menuItem, quantity);
    }

    public void removeItem(String customerId, int menuItemId) {
        Cart cart = getCart(customerId);
        cart.removeItem(menuItemId);
    }

    public void updateItemQuantity(String customerId, int menuItemId, int quantity) {
        Cart cart = getCart(customerId);
        cart.updateQuantity(menuItemId, quantity);
    }

    public void clearCart(String customerId) {
        getCart(customerId).clear();
    }

    public Cart viewCart(String customerId) {
        return getCart(customerId);
    }
}
