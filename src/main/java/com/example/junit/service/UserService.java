package com.example.junit.service;

import com.example.junit.dto.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserService {
    private final List<User> users = new ArrayList<>();

    public List<User> getAll() {
        return users;
    }

    public boolean add(User user) {
        return users.add(user);
    }
}