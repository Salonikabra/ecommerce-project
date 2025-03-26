package com.example.ecommerce.controller;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private ProductService productService;


    @Autowired
    private UserService userService;

    @ModelAttribute
    public void getUserDetails(Principal principal,Model model)
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


    @GetMapping("/")
    public String index()
    {
        return "index";
    }
    @GetMapping("/signin")
    public String login()
    {
        return "login";
    }
    @GetMapping("/register")
    public String register()
    {
        return "register";
    }
    @GetMapping("/products")
    public String products(Model model, @RequestParam(value="category",defaultValue = "") String category)
    {
        model.addAttribute("categories",categoryService.getAllActiveCategory());
        model.addAttribute("products",productService.getAllActiveProducts(category));
        model.addAttribute("paramValue",category);
        return "product";
    }
    @GetMapping("/view/{id}")
    public String viewproduct(@PathVariable int id,Model model) {
        model.addAttribute("product", productService.getProductById(id));


        return "view_product";
    }
    @PostMapping("/saveuser")
    public String saveUser(@RequestParam("name") String name,@RequestParam("mobile") String mobile
                           ,@RequestParam("email") String email,@RequestParam("address") String address,
                           @RequestParam("city") String city,@RequestParam("state") String state,@RequestParam("pincode") String pincode,
                           @RequestParam("password") String password,@RequestParam(value="image",required=false)MultipartFile file,
                           RedirectAttributes redirectAttributes)
    {
        String filename = "default.png"; // Default image
        if (file != null && !file.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            filename = file.getOriginalFilename();
            String filePath = uploadDir + filename;

            try {
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error_msg", "File upload failed!");
                return "redirect:/admin/product";
            }
        }
        User user1=new User();

        user1.setName(name);
        user1.setMobile(mobile);
        user1.setAddress(address);
        user1.setEmail(email);
        user1.setCity(city);
        user1.setState(state);
        user1.setPincode(pincode);
        user1.setPassword(password);
        user1.setImage(filename);
        User user2=userService.saveUser(user1);

        if(!ObjectUtils.isEmpty(user2))
        {
            redirectAttributes.addFlashAttribute("success"," yeah!! Registered Successfully please log in....");

        }
        else
        {
            redirectAttributes.addFlashAttribute("failure","oh no!!,,,something goes wrong");


        }
        return "redirect:/register";
    }








}
