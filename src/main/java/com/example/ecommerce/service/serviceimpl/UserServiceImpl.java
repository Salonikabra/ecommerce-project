package com.example.ecommerce.service.serviceimpl;

import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
     @Autowired
    private PasswordEncoder bCryptPasswordEncoder;
    @Override
    public User saveUser(User user) {
        user.setRole("ROLE_USER");
        user.setIsenable(true);
        String encodepassword=bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodepassword);
        return userRepository.save(user);

    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getUsers(String role) {
         return userRepository.findByRole(role);
    }

}
