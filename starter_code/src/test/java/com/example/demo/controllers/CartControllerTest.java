package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import com.example.demo.model.requests.ModifyCartRequest;

import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartControllerTest {
    private static CartController cartController;
    private static UserRepository userRepository = mock(UserRepository.class);
    private static CartRepository cartRepository = mock(CartRepository.class);
    private static ItemRepository itemRepository = mock(ItemRepository.class);

    private static Item item;
    private static Long ITEM_ID = 1L;
    private static String ITEM_NAME = "testItem";
    private static String DESCRIPTION = "Test item description.";
    private static BigDecimal PRICE = new BigDecimal(10.00);
    private static int QUANTITY = 2;

    private static User user;
    private static String USER_NAME = "testUser";
    private static Long USER_ID = 1L;

    private static Cart cart;

    @BeforeClass
    public static void setup () {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        cart = new Cart();
        cart.setTotal(new BigDecimal(0.0));


        user = new User();
        user.setId(USER_ID);
        user.setUsername(USER_NAME);
        user.setCart(cart);

        item = new Item();
        item.setId(ITEM_ID);
        item.setName(USER_NAME);
        item.setDescription(DESCRIPTION);
        item.setPrice(PRICE);

        when(userRepository.findByUsername(USER_NAME)).thenReturn(user);
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
    }

    @Test
    @Order(1)
    public void add_to_cart_happy_path() {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(item.getId());
        cartRequest.setQuantity(QUANTITY);
        cartRequest.setUsername(USER_NAME);

        final ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        BigDecimal quantity = new BigDecimal(QUANTITY);
        assertEquals(user.getCart().getTotal(), quantity.multiply(PRICE));
        assertEquals(user.getCart().getItems().size(), QUANTITY);
    }

    @Test
    @Order(2)
    public void remove_from_cart_happy() {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(item.getId());
        cartRequest.setUsername(USER_NAME);
        user.getCart().removeItem(item);
        final ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getCart().getTotal(), new BigDecimal(10.0));
        assertEquals(user.getCart().getItems().size(), 1);
    }
}
