package com.example.ecommerce.service.serviceimpl;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ProductServiceimpl implements ProductService
{

    @Autowired
   private ProductRepository productRepository;
    @Override
    public Product saveProduct(Product product)
    {
         return productRepository.save(product);

    }

    @Override
    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    @Override
    public boolean deleteProduct(int id) {
         Product product=productRepository.findById(id).orElse(null);
         if(!ObjectUtils.isEmpty(product))
         {
             productRepository.delete(product);
             return true;
         }
         return false;
    }

    @Override
    public Product getProductById(int id) {
        Product product=productRepository.findById(id).orElse(null);
        return product;

    }

    @Override
    public List<Product> getAllActiveProducts(String category) {
        List<Product> products=null;
        if(ObjectUtils.isEmpty(category))
        {
            products=productRepository.findByIsactiveTrue();
        }
        else
        {
           products= productRepository.findByCategory(category);
        }
        return products;
    }
}
