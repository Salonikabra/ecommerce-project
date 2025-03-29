package com.example.ecommerce.service;

import com.example.ecommerce.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {


    public User saveUser(User user);
    public User getUserByEmail(String email);
    public List<User> getUsers(String  role);

   public Boolean updateAccountStatus(int id, Boolean status);
   public void increaseFailedAttempt(User user);
   public void userAccountLock(User user);
   public boolean unlockAccountTimeExpired(User user);
   public void resetAttempt(int userId);

    public void updateUserResetToken(String email, String resetToken);
    public User getUserByToken(String token);
    public User updateUser(User user);
    public User updateUserProfile(User user);
    public User saveAdmin(User user);
    public boolean existEmail(String email);


}
