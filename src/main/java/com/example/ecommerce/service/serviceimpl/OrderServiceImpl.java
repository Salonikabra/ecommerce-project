package com.example.ecommerce.service.serviceimpl;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.OrderAddress;
import com.example.ecommerce.model.OrderRequest;
import com.example.ecommerce.model.ProductOrder;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductOrderRepository;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.util.CommonUtil;
import com.example.ecommerce.util.OrderStatus;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public void saveOrder(Integer userid, OrderRequest orderRequest) {
        List<Cart> carts=cartRepository.findByUserId(userid);

        for(Cart cart:carts)
        {
            ProductOrder order=new ProductOrder();
            order.setOrderid(UUID.randomUUID().toString());
            order.setOrderdate(LocalDate.now());
            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountprice());
            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());
            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymenttype(orderRequest.getPaymenttype());

            OrderAddress address=new OrderAddress();
            address.setFirstname(orderRequest.getFirstname());
            address.setLastname(orderRequest.getLastname());
            address.setEmail(orderRequest.getEmail());
            address.setMobile(orderRequest.getMobile());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setPincode(orderRequest.getPincode());
            address.setState(orderRequest.getState());

            order.setOrderAddress(address);

           ProductOrder saveOrder= orderRepository.save(order);
           try {
               commonUtil.sendMailForProductOrder(saveOrder, "successfull");
           }
           catch(Exception e)
           {
               e.printStackTrace();
           }





        }

    }

    @Override
    public List<ProductOrder> getOrderByUser(Integer userId) {

        List<ProductOrder> orders=orderRepository.findByUserId(userId);
        return orders;
    }

    @Override
    public ProductOrder updateOrderService(Integer id, String status) {

        Optional<ProductOrder> findById=orderRepository.findById(id);
        if(findById.isPresent())
        {
            ProductOrder productOrder=findById.get();
            productOrder.setStatus(status);
             return orderRepository.save(productOrder);

        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable=PageRequest.of(pageNo,pageSize);
        return orderRepository.findAll(pageable);
    }


}
