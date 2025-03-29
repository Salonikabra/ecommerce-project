package com.example.ecommerce.service.serviceimpl;

import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        String encodepassword = bCryptPasswordEncoder.encode(user.getPassword());
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

    @Override
    public Boolean updateAccountStatus(int id, Boolean status) {
        Optional<User> findByUser = userRepository.findById(id);
        if (findByUser.isPresent()) {
            User userdetail = findByUser.get();
            userdetail.setIsenable(status);
            userRepository.save(userdetail);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFailedAttempt(User user) {
        int attempt = user.getFailedAttempt() + 1;
        user.setFailedAttempt(attempt);
        userRepository.save(user);

    }

    @Override
    public void userAccountLock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);

    }

    @Override
    public boolean unlockAccountTimeExpired(User user) {
        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
        long currentTime = System.currentTimeMillis();
        if (unlockTime < currentTime) {
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }


        return false;
    }

    @Override
    public void resetAttempt(int userId) {

    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        User user = userRepository.findByEmail(email);
        user.setResetToken(resetToken);
        userRepository.save(user);
    }

    @Override
    public User getUserByToken(String token) {
        return userRepository.findByResetToken(token);


    }

    @Override
    public User updateUser(User user) {

        return userRepository.save(user);

    }

    @Override
    public User updateUserProfile(User user) {
        User dbUser = userRepository.findById(user.getId()).get();
        if (!ObjectUtils.isEmpty(dbUser)) {
            dbUser.setName(user.getName());
            dbUser.setMobile(user.getMobile());
            dbUser.setAddress(user.getAddress());
            dbUser.setCity(user.getCity());
            dbUser.setState(user.getState());
            dbUser.setPincode(user.getPincode());
            dbUser = userRepository.save(dbUser);


        }
        return dbUser;
    }

    @Override
    public User saveAdmin(User user) {
        user.setRole("ROLE_ADMIN");
        user.setIsenable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        String encodepassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodepassword);
        return userRepository.save(user);


    }

    @Override
    public boolean existEmail(String email) {
        return userRepository.existsByEmail(email);

    }
}

