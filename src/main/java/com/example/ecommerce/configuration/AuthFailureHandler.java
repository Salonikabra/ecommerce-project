package com.example.ecommerce.configuration;

import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.rmi.ServerException;

/*@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServerException, ServletException {

        String email=request.getParameter("username");
        User user=userRepository.findByEmail(email);
        if(user.getIsenable())
        {
            if(user.getAccountNonLocked())
            {
                if(user.getFailedAttempt()<AppConstant.ATTEMPT_TIME)
                {
                    userService.increaseFailedAttempt(user);


                }
                else
                {
                    userService.userAccountLock(user);
                    exception=new LockedException("your account is locked || failed attempt!!");
                }

            }else
            {
                if(userService.unlockAccountTimeExpired(user))
                {
                    exception=new LockedException("your account is unlocked || please try to login");
                }
                else
                {
                    exception = new LockedException("your account is locked, please try after sometime");

                }


            }

        }else {
            exception = new LockedException("your account is inactive");
        }
        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request,response,exception);





    }


}*/
@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        String email = request.getParameter("username");
        User user = userRepository.findByEmail(email);

        if (user == null) {
            super.setDefaultFailureUrl("/signin?error=invalid_credentials");
        } else if (!user.getIsenable()) {
            super.setDefaultFailureUrl("/signin?error=account_inactive");
        } else if (!user.getAccountNonLocked()) {
            if (userService.unlockAccountTimeExpired(user)) {
                super.setDefaultFailureUrl("/signin?error=account_unlocked");
            } else {
                super.setDefaultFailureUrl("/signin?error=account_locked");
            }
        } else {
            if (user.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
                userService.increaseFailedAttempt(user);
            } else {
                userService.userAccountLock(user);
                super.setDefaultFailureUrl("/signin?error=too_many_attempts");
            }
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}

