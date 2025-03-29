package com.example.ecommerce.controller;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.util.CommonUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private ProductService productService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CartService cartService;


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
           Integer countCart=cartService.getCountCart(userdetails.getId());
            model.addAttribute("countCart",countCart);
        }

        List<Category> categories1=categoryService.getAllCategory();
        model.addAttribute("categories",categories1);


    }


    @GetMapping("/")
    public String index(Model model)
    {
        List<Category> allActiveCategory=categoryService.getAllActiveCategory().stream()
                .sorted((c1,c2)->c2.getId().compareTo(c1.getId()))
                .limit(6).toList();
        List<Product> allActiveProducts=productService.getAllActiveProducts("").stream()
                .sorted((p1,p2)->p2.getId().compareTo(p1.getId()))
                .limit(8).toList();
        model.addAttribute("categories",allActiveCategory);
        model.addAttribute("products",allActiveProducts);

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
    public String products(Model model, @RequestParam(value="category",defaultValue = "") String category,
                           @RequestParam(name="pageNo",defaultValue="0") Integer pageNo,
                           @RequestParam(name="pageSize",defaultValue="6") Integer pageSize,
                           @RequestParam(defaultValue = " ") String ch)
    {
        model.addAttribute("categories",categoryService.getAllActiveCategory());
    //   model.addAttribute("products",productService.getAllActiveProducts(category));
        model.addAttribute("paramValue",category);
        Page<Product> page=null;
        if(StringUtils.isEmpty(ch))
        {
           page= productService.getAllActiveProductPagination(pageNo,pageSize,category);
        }
        else
        {
            page= productService.searchActiveProductPagination(pageNo,pageSize,category,ch);
        }



        List<Product> products=page.getContent();
        model.addAttribute("products",products);
        model.addAttribute("productSize",products.size());
        model.addAttribute("pageNo",page.getNumber());
        model.addAttribute("totalElements",page.getTotalElements());
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("isFirst",page.isFirst());
        model.addAttribute("isLast",page.isLast());
        model.addAttribute("pageSize",pageSize);


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

       boolean b= userService.existEmail(email);
       if(b)
       {
           redirectAttributes.addFlashAttribute("error","email already exist");
           return "redirect:/register";
       }


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
           User user1 = new User();

           user1.setName(name);
           user1.setMobile(mobile);
           user1.setAddress(address);
           user1.setEmail(email);
           user1.setCity(city);
           user1.setState(state);
           user1.setPincode(pincode);
           user1.setPassword(password);
           user1.setImage(filename);


           User user2 = userService.saveUser(user1);

           if (!ObjectUtils.isEmpty(user2)) {
               redirectAttributes.addFlashAttribute("success", " yeah!! Registered Successfully please log in....");

           } else {
               redirectAttributes.addFlashAttribute("failure", "oh no!!,,,something goes wrong");


           }
           


           return "redirect:/register";


    }
    //forgot password
    @GetMapping("/forgot-password")
    public String showForgotPassword()
    {
        return "forgot_password";

    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes,
                                        HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
      User useremail=  userService.getUserByEmail(email);
      if(ObjectUtils.isEmpty(useremail)) {
          redirectAttributes.addFlashAttribute("error", "oh no! invalid email..please enter valid email");

      }
      else
      {
          String resetToken= UUID.randomUUID().toString();
          userService.updateUserResetToken(email,resetToken);
          //generate url:http://localhost:8081/reset-password?token=3435454

          String url=CommonUtil.generateURL(request)+"/reset-password?token="+resetToken;

          Boolean sendEmail=commonUtil.sendMail(url,email);




           Boolean sendMail=CommonUtil.sendMail();
           if(sendMail)
           {
               redirectAttributes.addFlashAttribute("msg","email sent successfully");

           }
           else
           {
               redirectAttributes.addFlashAttribute("error1","something wrong on server");
           }




      }
        return "redirect:/forgot-password";

    }









    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token,RedirectAttributes redirectAttributes,Model model)
    {

        User user=userService.getUserByToken(token);
        if(ObjectUtils.isEmpty(user))
        {
            redirectAttributes.addFlashAttribute("error","your link is invalid");
            return "redirect:/forgot-password";
        }
        model.addAttribute("token",token);

        return "reset_password";
    }


    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,@RequestParam String password,
                                RedirectAttributes redirectAttributes)
    {

        User user=userService.getUserByToken(token);
        if(ObjectUtils.isEmpty(user))
        {
            redirectAttributes.addFlashAttribute("error","your link is invalid");
            return "redirect:/reset-password";
        }
        else {

            user.setPassword(passwordEncoder.encode(password));
            user.setResetToken(null);
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("success","Password change successfully");
            redirectAttributes.addFlashAttribute("msg","password change successfully");
            return "redirect:/message";

        }


        



    }
    @GetMapping("/message")
    public String message()
    {
        return "message";
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch,Model model)
    {
        List<Product> products=productService.searchProduct(ch);
        model.addAttribute("products",products);
        return "product";


    }











}
