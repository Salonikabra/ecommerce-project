package com.example.ecommerce.service.serviceimpl;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

     @Autowired
     private UserRepository userRepository;
     @Autowired
    private ProductRepository productRepository;


    @Override
    public Cart saveCart(Integer productId, Integer userId) {

        User user=userRepository.findById(userId).get();
        Product product=productRepository.findById(productId).get();
        Cart cartStatus=cartRepository.findByProductIdAndUserId(productId,userId);
        Cart cart=null;
        if(ObjectUtils.isEmpty(cartStatus))
        {
            cart=new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(1);
            cart.setTotalPrice(product.getDiscountprice());

        }
        else {
           cart= cartStatus;
           cart.setQuantity(cart.getQuantity()+1);
           cart.setTotalPrice(cart.getQuantity()*cart.getProduct().getDiscountprice());
        }
        Cart savecart=cartRepository.save(cart);
        return savecart;
    }

    @Override
    public List<Cart> getCartByUser(Integer userId) {

      List<Cart> carts=cartRepository.findByUserId(userId);
      Double totalOrderPrice=0.0;
      List<Cart> updateCarts=new ArrayList<>();
      for(Cart c:carts)
      {
          Double totalPrice=(c.getProduct().getDiscountprice() * c.getQuantity());
          c.setTotalPrice(totalPrice);
          totalOrderPrice+=totalPrice;
          c.setTotalOrderPrice(totalOrderPrice);
          updateCarts.add(c);

      }
      return updateCarts;



    }

    @Override
    public Integer getCountCart(Integer userId) {

        Integer count=cartRepository.countByUserId(userId);

        return count;
    }

   /* @Override
    public void updateQuantity(String sy, Integer cid) {
        Cart cart=cartRepository.findById(cid).get();
        int updateQuantity;
        if(sy.equalsIgnoreCase("de"))
        {
         //   updateQuantity=cart.getQuantity()-1;
            if(updateQuantity<=0)
            {
                cartRepository.delete(cart);

            }
            updateQuantity=cart.getQuantity()-1;
        }
        else
        {
            updateQuantity=cart.getQuantity()+1;
        }
        cart.setQuantity(updateQuantity);
        cartRepository.save(cart);
    }*/
   @Override
   public void updateQuantity(String sy, Integer cid) {
       Cart cart = cartRepository.findById(cid).orElse(null);

       if (cart == null) {
           return; // If cart item doesn't exist, return
       }

       int updatedQuantity = cart.getQuantity();

       if (sy.equalsIgnoreCase("de")) {
           updatedQuantity -= 1;
           if (updatedQuantity <= 0) {
               cartRepository.delete(cart); // Delete if quantity reaches 0
               return; // Stop execution after deletion
           }
       } else {
           updatedQuantity += 1;
       }

       cart.setQuantity(updatedQuantity);
       cart.setTotalPrice(updatedQuantity * cart.getProduct().getDiscountprice());
       cartRepository.save(cart);
   }


}
