package com.example.ecommerce.controller;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String  home()
    {
        return "user/home";


    }
    @ModelAttribute
    public void getUserDetails(Principal principal, Model model)
    {
        if(principal!=null)
        {
            String email=principal.getName();
            User userdetails=userService.getUserByEmail(email);
            model.addAttribute("user",userdetails);
        }
        List<Category> categories1=categoryService.getAllCategory();
        model.addAttribute("categories",categories1);


    }









}
