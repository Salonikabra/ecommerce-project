package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductOrder;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);
    public List<Product> getAllProducts();
    public boolean deleteProduct(int id);
    public Product getProductById(int id);
    public List<Product> getAllActiveProducts(String category);
    public List<Product> searchProduct(String ch);
    public Page<Product> getAllActiveProductPagination(Integer pageNo,Integer pageSize,String category);
    public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize);

    public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize,String category,String ch);
}

