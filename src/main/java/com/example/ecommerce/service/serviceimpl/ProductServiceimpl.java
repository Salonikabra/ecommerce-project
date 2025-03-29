package com.example.ecommerce.service.serviceimpl;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductOrder;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Override
    public List<Product> searchProduct(String ch) {
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch);

    }

   /* @Override
    public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize,String category) {
        Pageable pageable=PageRequest.of(pageNo,pageSize);
        Page<Product> pageProduct=null;
        if(ObjectUtils.isEmpty(category))
        {
            pageProduct=productRepository.findByIsactiveTrue(pageable);
        }
        else
        {
            pageProduct= productRepository.findByCategory(pageable,category);
        }
        return pageProduct;
    }*/
   @Override
   public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category) {
       // Ensure pageNo is at least 0 and pageSize is at least 1
       pageNo = (pageNo == null || pageNo < 0) ? 0 : pageNo;
       pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize; // Default to 10 if invalid

       Pageable pageable = PageRequest.of(pageNo, pageSize);
       Page<Product> pageProduct = null;

       if (ObjectUtils.isEmpty(category)) {
           pageProduct = productRepository.findByIsactiveTrue(pageable);
       } else {
           pageProduct = productRepository.findByCategory(pageable, category);
       }

       return pageProduct;
   }

    @Override
    public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable=PageRequest.of(pageNo,pageSize);
        return productRepository.findAll(pageable);

    }

    @Override
    public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize,String category,String ch) {
       Page<Product> pageProduct=null;
       Pageable pageable=PageRequest.of(pageNo,pageSize);
        pageProduct=productRepository.findByIsactiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch,pageable);
      /* if(ObjectUtils.isEmpty(category))
       {
           pageProduct=productRepository.findByIsactiveTrue(pageable);
       }
       else
       {
           pageProduct=productRepository.findByCategory(pageable,category);
       }*/
       return pageProduct;
    }

}
