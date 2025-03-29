package com.example.ecommerce.repository;

import com.example.ecommerce.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder,Integer> {

    public List<ProductOrder> findByUserId(Integer userId);


}
