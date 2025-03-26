package com.example.ecommerce.service;

import com.example.ecommerce.model.User;

import java.util.List;

public interface UserService {


    public User saveUser(User user);
    public User getUserByEmail(String email);
    public List<User> getUsers(String  role);

}
