package com.example.ecommerce.controller;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.util.CommonUtil;
import com.example.ecommerce.util.OrderStatus;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.print.attribute.standard.PrinterInfo;
import java.awt.desktop.PrintFilesEvent;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String home() {
        return "user/home";


    }

    @ModelAttribute
    public void getUserDetails(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            User userdetails = userService.getUserByEmail(email);
            model.addAttribute("user", userdetails);
            Integer countCart = cartService.getCountCart(userdetails.getId());
            model.addAttribute("countCart", countCart);

        }
        List<Category> categories1 = categoryService.getAllCategory();
        model.addAttribute("categories", categories1);


    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, RedirectAttributes redirectAttributes) {
        Cart savecart = cartService.saveCart(pid, uid);
        if (ObjectUtils.isEmpty(savecart)) {
            redirectAttributes.addFlashAttribute("error", "product add to cart failed");
        } else {
            redirectAttributes.addFlashAttribute("success", "product added to cart");
        }

        return "redirect:/view/" + pid;
    }


    /*  @GetMapping("/cart")
      public String loadCartPage(Principal principal,Model model)
      {
         User user=getLoggedInUserDetails(principal);
         List<Cart> carts=cartService.getCartByUser(user.getId());
         model.addAttribute("carts",carts);
         Double totalOrderPrice=carts.get(carts.size()-1).getTotalOrderPrice();
         model.addAttribute("totalOrderPrice",totalOrderPrice);
          return "/user/cart";
      }*/
    @GetMapping("/cart")
    public String loadCartPage(Principal principal, Model model) {
        User user = getLoggedInUserDetails(principal);
        List<Cart> carts = cartService.getCartByUser(user.getId());

        model.addAttribute("carts", carts);

        // Check if carts is empty before accessing elements
        if (carts != null && !carts.isEmpty()) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        } else {
            model.addAttribute("totalOrderPrice", 0.0);
        }

        return "/user/cart";
    }


    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
        cartService.updateQuantity(sy, cid);

        return "redirect:/user/cart";

    }


    private User getLoggedInUserDetails(Principal principal) {

        String email = principal.getName();
        User userDtls = userService.getUserByEmail(email);
        return userDtls;


    }

    @GetMapping("/orders")
    public String orderPage(Model model, Principal principal) {
        User user = getLoggedInUserDetails(principal);
        List<Cart> carts = cartService.getCartByUser(user.getId());

        model.addAttribute("carts", carts);

        // Check if carts is empty before accessing elements
        if (carts != null && !carts.isEmpty()) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 250 + 100;
            model.addAttribute("totalOrderPrice", totalOrderPrice);
            model.addAttribute("orderPrice", orderPrice);
        } else {
            model.addAttribute("orderPrice", 0.0);
        }


        return "/user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest, Principal principal) throws Exception {
        //   System.out.println(orderRequest);
        User user = getLoggedInUserDetails(principal);
        orderService.saveOrder(user.getId(), orderRequest);
        return "user/success";

    }

    @GetMapping("/user-orders")
    public String myOrders(Model model, Principal principal) {
        User logInUser = getLoggedInUserDetails(principal);
        List<ProductOrder> orders = orderService.getOrderByUser(logInUser.getId());
        model.addAttribute("orders", orders);
        return "/user/my_orders";

    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, RedirectAttributes redirectAttributes) throws MessagingException, UnsupportedEncodingException {
        OrderStatus[] values = OrderStatus.values();
        String status = null;
        for (OrderStatus orderStatus : values) {
            if (orderStatus.getId().equals(st))
                status = orderStatus.getName();
        }
        ProductOrder updateOrderService = orderService.updateOrderService(id, status);
        try {
            commonUtil.sendMailForProductOrder(updateOrderService, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!ObjectUtils.isEmpty(updateOrderService)) {
            redirectAttributes.addFlashAttribute("success", "status updated");

        } else {
            redirectAttributes.addFlashAttribute("error", "something went wrong on server");

        }


        return "redirect:/user/user-orders";

    }

    @GetMapping("/profile")
    public String profile() {
        return "/user/profile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        userService.updateUserProfile(user);
        redirectAttributes.addFlashAttribute("success", "yeah....!! data updated successfully");
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword,
                                 Principal principal, RedirectAttributes redirectAttributes) {
        User loginuser = getLoggedInUserDetails(principal);
        boolean matches = passwordEncoder.matches(currentPassword, loginuser.getPassword());
        if (matches) {
            String encodepassword = passwordEncoder.encode(newPassword);
            loginuser.setPassword(encodepassword);
            User user1 = userService.updateUser(loginuser);
            if (!ObjectUtils.isEmpty(user1)) {
                redirectAttributes.addFlashAttribute("success1", "password change successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "something went wrong");
            }
        } else {
            redirectAttributes.addFlashAttribute("error1", "current password incorrect");
        }






        return "redirect:/user/profile";
    }


}
