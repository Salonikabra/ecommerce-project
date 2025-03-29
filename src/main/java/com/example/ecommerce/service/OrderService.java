package com.example.ecommerce.service;

import com.example.ecommerce.model.OrderRequest;
import com.example.ecommerce.model.ProductOrder;
import org.hibernate.query.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    public void saveOrder(Integer userid, OrderRequest orderRequest)throws Exception;

    public List<ProductOrder> getOrderByUser(Integer userId);

    public ProductOrder updateOrderService(Integer id,String st);

    public List<ProductOrder> getAllOrders();

    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo,Integer pageSize);


}
