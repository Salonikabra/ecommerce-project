package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);
    public List<Product> getAllProducts();
    public boolean deleteProduct(int id);
    public Product getProductById(int id);
    public List<Product> getAllActiveProducts(String category);


}

