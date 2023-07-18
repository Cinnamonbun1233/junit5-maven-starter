package com.example.junit.service;

import com.example.junit.dto.User;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserServiceTest {
    private UserService userService;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Before all: ");
    }

    @BeforeEach
    void before() {
        System.out.println("Before each: " + this);
        userService = new UserService();
    }

    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this);
        List<User> users = userService.getAll();

        assertTrue(users.isEmpty());
    }
    
    @Test
    void usersSizeIfUsersAdded() {
        System.out.println("Test 2: " + this);
        userService.add(new User());
        userService.add(new User());

        List<User> users = userService.getAll();
        assertEquals(2, users.size());
    }

    @AfterEach
    void after() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    static void afterAll() {
        System.out.println("After all: ");
    }
}