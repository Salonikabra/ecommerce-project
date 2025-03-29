package com.example.ecommerce.util;

import com.example.ecommerce.model.ProductOrder;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

@Component
public class CommonUtil {


    @Autowired
    private  JavaMailSender mailSender;


    @Autowired
    private UserService userService;


    public static Boolean sendMail()
    {
        return true;
    }

    public static String generateURL(HttpServletRequest request) {

//http://localhost:8081/forgot-passwod

        String siteurl=request.getRequestURL().toString();
        return siteurl.replace(request.getServletPath(),"");//http://localhost:8081


    }

    public  Boolean sendMail(String url, String recipientEmail) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message=mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(message);
        helper.setFrom("kabrasaloni06@gmail.com","Shopping Cart");
        helper.setTo(recipientEmail);
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password. Click the link below:</p>"
                + "<p><a href=\"" + url + "\">Reset Password</a></p>"
                + "<p>If you did not request this, please ignore this email.</p>"
                + "<br><p>Best Regards,<br>Shopping Cart Team</p>";
        helper.setSubject("Password Reset");
        helper.setText(content,true);
        mailSender.send(message);
        return true;
    }








   /* public boolean sendMailForProductOrder(ProductOrder order) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message=mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(message);
        helper.setFrom("kabrasaloni06@gmail.com","Shopping Cart");
        helper.setTo(order.getOrderAddress().getEmail());
        String msg="<p>thank you order successflly.</p>"
                +"<p>product details:</p>"
                +"<p>name:</p>"
                +"<p>category " +
                "quantity" +
                "+price" +
                "+payment type;"


        helper.setSubject("Product Order Status");
        helper.setText(msg,true);
        mailSender.send(message);
        return true;

    }*/

 /*   String msg = "<p>Thank you! Your order was <b>[[OrderStatus]]</b>.</p>"
            + "<p>Product Details:</p>"
            + "<p>Name:[[productname]]</p>"
            + "<p>Category:[[category]]</p>"
            + "<p>Quantity:[[quantity]]</p>"
            + "<p>Price:[[price]]</p>"
            + "<p>Payment Type:[[paymenttype]]</p>";





    public boolean sendMailForProductOrder(ProductOrder order,String status) throws MessagingException, UnsupportedEncodingException {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("kabrasaloni06@gmail.com", "Shopping Cart");
            helper.setTo(order.getOrderAddress().getEmail());
            helper.setSubject("Product Order Status");



        msg=msg.replace("[[orderStatus]]",status);
            msg=msg.replace("[[productname]]",order.getProduct().getTitle());
        msg=msg.replace("[[category]]",order.getProduct().getCategory());
        msg=msg.replace("[[quantity]]",order.getQuantity().toString());
        msg=msg.replace("[[price]]",order.getPrice().toString());
        msg=msg.replace("[[paymenttype]]",order.getPaymenttype());


            helper.setText(msg, true); // Set HTML content
            mailSender.send(message);

            return true;
        }*/
 public boolean sendMailForProductOrder(ProductOrder order, String status) throws MessagingException, UnsupportedEncodingException {
     MimeMessage message = mailSender.createMimeMessage();
     MimeMessageHelper helper = new MimeMessageHelper(message, true);

     helper.setFrom("kabrasaloni06@gmail.com", "Shopping Cart");
     helper.setTo(order.getOrderAddress().getEmail());
     helper.setSubject("Product Order Status");

     // Properly formatted HTML email
     String msg = "<div style='font-family: Arial, sans-serif; color: #333;'>"
             + "<h2 style='color: #2c3e50;'>Thank You! Your order was <b>[[orderStatus]]</b>.</h2>"
             + "<p><b>Product Details:</b></p>"
             + "<p><b>Name:</b> [[productname]]</p>"
             + "<p><b>Category:</b> [[category]]</p>"
             + "<p><b>Quantity:</b> [[quantity]]</p>"
             + "<p><b>Price:</b> ₹[[price]]</p>"
             + "<p><b>Payment Type:</b> [[paymenttype]]</p>"
             + "<br><p>If you have any questions, feel free to reply to this email.</p>"
             + "<p>Best Regards,<br><b>Shopping Cart Team</b></p>"
             + "</div>";

     // Replacing placeholders with actual values
     msg = msg.replace("[[orderStatus]]", status);
     msg = msg.replace("[[productname]]", order.getProduct().getTitle());
     msg = msg.replace("[[category]]", order.getProduct().getCategory());
     msg = msg.replace("[[quantity]]", order.getQuantity().toString());
     msg = msg.replace("[[price]]", order.getPrice().toString());
     msg = msg.replace("[[paymenttype]]", order.getPaymenttype());

     helper.setText(msg, true); // Set as HTML content
     mailSender.send(message);

     return true;
 }
    public User getLoggedInUserDetails(Principal principal) {

        String email = principal.getName();
        User userDtls = userService.getUserByEmail(email);
        return userDtls;


    }





}


