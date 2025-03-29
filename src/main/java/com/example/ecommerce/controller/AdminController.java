package com.example.ecommerce.controller;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductOrder;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.*;
import com.example.ecommerce.util.CommonUtil;
import com.example.ecommerce.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.persistence.GeneratedValue;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/product")
    public String addproduct(Model model) {
        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("categories", categories);
        return "admin/add_product";
    }


    @GetMapping("/category")
    public String category(Model model, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize"
            , defaultValue = "5") Integer pageSize) {

        //  model.addAttribute("categories", categoryService.getAllCategory());
        Page<Category> page = categoryService.getAllCategoryPagination(pageNo, pageSize);
        List<Category> categories = page.getContent();
        model.addAttribute("categories", categories);
        //   model.addAttribute("productSize",products.size());
        model.addAttribute("pageNo", page.getNumber());
        //   model.addAttribute("pageNo",pageNo);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());
        model.addAttribute("pageSize", pageSize);


        return "admin/category";

    }

    @GetMapping("/loadedCategory/{id}")
    public String loadedcategory(@PathVariable int id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));

        return "admin/edit_category";
    }


    //saving category details
    @PostMapping("/saveCategory")
    private String saveCategory(
            @RequestParam("name") String name,
            @RequestParam(value = "imagename", required = false) MultipartFile file,
            @RequestParam("isactive") boolean isactive,
            RedirectAttributes redirectAttributes) {

        // Check if category already exists
        if (name == null || name.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error3_msg", "Category name cannot be empty!");
            return "redirect:/admin/category";
        }
        Boolean existcategory = categoryService.existCategory(name);
        if (existcategory) {
            redirectAttributes.addFlashAttribute("error_msg", "Category name already exists");
            return "redirect:/admin/category";
        }

        Category category = new Category();
        category.setName(name);
        category.setIsactive(isactive);

        String fileName = "default.png"; // Default image

        // Process file only if it's provided and not empty
        if (file != null && !file.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // Create directory if it doesn't exist
            }

            fileName = /*System.currentTimeMillis() + "_" + */file.getOriginalFilename();
            String filePath = uploadDir + fileName;

            try {
                file.transferTo(new File(filePath)); // Save file
            } catch (Exception e) {
                System.out.println("File upload failed: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error_msg", "File upload failed!");
                return "redirect:/admin/category";
            }
        }

        category.setImagename(fileName); // Save file name (uploaded or default)

        categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("success_msg", "Category added successfully!");
        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, RedirectAttributes redirectAttributes) {
        boolean deletecategory = categoryService.deleteCategory(id);
        if (deletecategory) {
//session.setAttribute("success","item deleted successfully");
            redirectAttributes.addFlashAttribute("success", "data deleted successfully");
        } else {
            //session.setAttribute("error","something goes wrong");
            redirectAttributes.addFlashAttribute("error", "something goes wrong");
        }
        return "redirect:/admin/category";

    }

    @PostMapping("/updateCategory")
    public String updateCategory(@RequestParam("id") int id,
                                 @RequestParam("name") String name,
                                 @RequestParam(value = "imagename", required = false) MultipartFile file,
                                 @RequestParam("isactive") boolean isActive,
                                 RedirectAttributes redirectAttributes) {
        // Fetch existing category
        Category category = categoryService.getCategoryById(id);

        if (category == null) {
            redirectAttributes.addFlashAttribute("error_msg", "Category not found!");
            return "redirect:/admin/category";
        }

        // Update name and active status
        category.setName(name);
        category.setIsactive(isActive);

        // Process image file
        String fileName = category.getImagename(); // Keep existing image if no new file is uploaded
        if (file != null && !file.isEmpty()) {
            fileName = file.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            try {
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Path path = Paths.get(uploadDir + fileName);
                Files.write(path, file.getBytes());
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error_msg", "File upload failed!");
                return "redirect:/admin/category";
            }
        }

        category.setImagename(fileName);

        // Save the updated category
        categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("success_msg", "Category updated successfully!");

        return "redirect:/admin/category";
    }

    //saveproduct
    @PostMapping("/saveProduct")
    public String saveProduct(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("stock") int stock,
            @RequestParam("category") String category,
            @RequestParam("isactive") Boolean isactive1,

            @RequestParam(value = "image", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {

        // Validate product title
        if (title == null || title.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error_msg", "Product title cannot be empty!");
            return "redirect:/admin/product";
        }

        // Validate description
        if (description == null || description.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error_msg", "Product description cannot be empty!");
            return "redirect:/admin/product";
        }

        // Validate price
        if (price <= 0) {
            redirectAttributes.addFlashAttribute("error_msg", "Product price must be greater than zero!");
            return "redirect:/admin/product";
        }

        // Validate quantity
        if (stock == 0) {
            redirectAttributes.addFlashAttribute("error_msg", "Product quantity cannot be empty!");
            return "redirect:/admin/product";
        }

        String fileName = "default.png"; // Default image
        if (file != null && !file.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            fileName = file.getOriginalFilename();
            String filePath = uploadDir + fileName;

            try {
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error_msg", "File upload failed!");
                return "redirect:/admin/product";
            }
        }
        int discount = 0;
        Double discountprice = price;

        discountprice = price;
        System.out.println(price);
        System.out.println(discountprice);
        // Create and save product
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        product.setImage(fileName);
        product.setDiscount(discount);
        product.setDiscountprice(discountprice);
        product.setIsactive(isactive1);

        Product savedProduct = productService.saveProduct(product);

        if (ObjectUtils.isEmpty(savedProduct)) {
            redirectAttributes.addFlashAttribute("error_msg", "Something went wrong while saving the product.");
        } else {
            redirectAttributes.addFlashAttribute("success_msg", "Product added successfully!");
        }

        return "redirect:/admin/product";
    }

    @GetMapping("/loadViewProducts")
    public String loadViewProduct(Model model, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize"
            , defaultValue = "5") Integer pageSize) {
   /*  List<Product> product1=productService.getAllProducts();
        model.addAttribute("products",product1);*/


        Page<Product> page = productService.getAllProductsPagination(pageNo, pageSize);

        //model.addAttribute("pageNo",pageNo);

        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());
        model.addAttribute("pageSize", pageSize);

     /*   List<Category> categories=page.getContent();
        model.addAttribute("categories",categories);
        //   model.addAttribute("productSize",products.size());
        model.addAttribute("pageNo",page.getNumber());
        model.addAttribute("totalElements",page.getTotalElements());
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("isFirst",page.isFirst());
        model.addAttribute("isLast",page.isLast());
        model.addAttribute("pageSize",pageSize);
*/


        return "admin/products";
    }

    @GetMapping("/deleteProducts/{id}")
    public String deleteProducts(@PathVariable int id, RedirectAttributes redirectAttributes) {
        if (productService.deleteProduct(id)) {
            redirectAttributes.addFlashAttribute("success", "product deleted successfully");


        } else {
            redirectAttributes.addFlashAttribute("error", "something goes wrong");
        }

        return "redirect:/admin/loadViewProducts";
    }

    @GetMapping("/editProducts/{id}")
    public String editProducts(@PathVariable int id, Model model) {
        model.addAttribute("product1", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategory());
        return "admin/edit_product";

    }

 /*   @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product,RequestParam("RedirectAttributes redirectAttributes)
    {




        return "redirect:/admin/loadViewProducts";

    }*/

    @PostMapping("/updateProduct")
    public String updateProduct(@RequestParam("id") int id,
                                @RequestParam("title") String title,
                                @RequestParam(value = "image", required = false) MultipartFile file,
                                @RequestParam("category") String category,
                                @RequestParam("price") double price,
                                @RequestParam("stock") int stock,
                                @RequestParam("description") String description,
                                @RequestParam("discount") int discount,
                                @RequestParam("discountprice") double discountprice,
                                @RequestParam("isactive") boolean isactive1,
                                RedirectAttributes redirectAttributes) {
        // Fetch existing category
        Product product = productService.getProductById(id);

        if (product == null) {
            redirectAttributes.addFlashAttribute("error_msg", "product not found!");
            return "redirect:/admin/editProducts/" + id;

        }
        if (discount < 0 || discount > 100) {
            redirectAttributes.addFlashAttribute("error_msg", "invalid discount");
        } else {

            Double discountprice1 = price * (discount / 100.0);
            Double originalprice = price - discountprice1;
            product.setDiscountprice(originalprice);
            product.setDiscount(discount);
        }
        product.setTitle(title);
        product.setCategory(category);
        product.setPrice(price);
        product.setStock(stock);
        product.setDescription(description);
        product.setIsactive(isactive1);


        // Process image file
        String fileName = product.getImage(); // Keep existing image if no new file is uploaded
        if (file != null && !file.isEmpty()) {
            fileName = file.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            try {
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Path path = Paths.get(uploadDir + fileName);
                Files.write(path, file.getBytes());
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error_msg", "File upload failed!");
                return "redirect:/admin/editProducts/" + id;

            }
        }

        product.setImage(fileName);

        // Save the updated category
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("success_msg", "product  updated successfully!");

        return "redirect:/admin/loadViewProducts";
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

    @GetMapping("/users")
    public String getAllUsers(Model model,@RequestParam Integer type) {
        List<User> users=null;
        if(type==1)
        {
           users= userService.getUsers("ROLE_USER");
        }else
        {
            users=userService.getUsers("ROLE_ADMIN");
        }
        model.addAttribute("userType",type);
        model.addAttribute("users", users);
        return "/admin/users";
    }

    @GetMapping("/updateSts")
    public String updateUserAccountStatus(@RequestParam Boolean status,
                                          @RequestParam int id,
                                          @RequestParam Integer type,
                                          RedirectAttributes redirectAttributes) {
        System.out.println(id + "start" + status);
        if (status == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid status value!");
            System.out.println(id + "null" + status);
            return "redirect:/admin/users?type="+type;
        }
        Boolean f = userService.updateAccountStatus(id, status);
        if (f) {
            System.out.println(id + "success" + status);
            redirectAttributes.addFlashAttribute("success", "Account status updated");

        } else {
            System.out.println(id + "error " + status);
            redirectAttributes.addFlashAttribute("error", "something went wrong");
        }

        return "redirect:/admin/users?type="+type;
    }

    @GetMapping("/orders")
    public String getAllOrders(Model model, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize"
            , defaultValue = "5") Integer pageSize) {
  /*      List<ProductOrder> orders=orderService.getAllOrders();
        model.addAttribute("orders",orders);*/
        Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
        model.addAttribute("orders", page.getContent());


        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());
        model.addAttribute("pageSize", pageSize);


        //   model.addAttribute("srch",false);
        return "admin/orders";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, RedirectAttributes redirectAttributes) throws MessagingException, UnsupportedEncodingException {
        OrderStatus[] values = OrderStatus.values();
        String status = null;
        for (OrderStatus orderStatus : values) {
            if (orderStatus.getId().equals(st))
                status = orderStatus.getName();
        }
        ProductOrder updateOrderService = orderService.updateOrderService(id, status);
        commonUtil.sendMailForProductOrder(updateOrderService, status);
        if (!ObjectUtils.isEmpty(updateOrderService)) {
            redirectAttributes.addFlashAttribute("success", "yeah....status updated successfully");

        } else {
            redirectAttributes.addFlashAttribute("error", "oh no!...status not updated");

        }


        return "redirect:/admin/orders";

    }

    @GetMapping("/add-admin")
    public String loadAdminAdd() {
        return "/admin/add_admin";

    }

    @PostMapping("/save-admin")
    public String saveAdmin(@RequestParam("name") String name, @RequestParam("mobile") String mobile
            , @RequestParam("email") String email, @RequestParam("address") String address,
                            @RequestParam("city") String city, @RequestParam("state") String state, @RequestParam("pincode") String pincode,
                            @RequestParam("password") String password, @RequestParam(value = "image", required = false) MultipartFile file,
                            RedirectAttributes redirectAttributes) {
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
        User user2 = userService.saveAdmin(user1);

        if (!ObjectUtils.isEmpty(user2)) {
            redirectAttributes.addFlashAttribute("success", " yeah!! Registered Successfully please log in....");

        } else {
            redirectAttributes.addFlashAttribute("failure", "oh no!!,,,something goes wrong");


        }
        return "redirect:/admin/add-admin";
    }

    @GetMapping("/profile")
    public String profile() {
        return "/admin/profile";
    }


    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        userService.updateUserProfile(user);
        redirectAttributes.addFlashAttribute("success", "yeah....!! data updated successfully");
        return "redirect:/admin/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword,
                                 Principal principal, RedirectAttributes redirectAttributes) {
        User loginuser = commonUtil.getLoggedInUserDetails(principal);
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






        return "redirect:/admin/profile";
    }






}






