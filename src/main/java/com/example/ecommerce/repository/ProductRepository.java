package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {

   // public Page<Product> findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch1, Pageable pageable);


    public List<Product> findByIsactiveTrue();

    public List<Product> findByCategory(String category);
   public List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch,String ch2);
    public Page<Product> findByIsactiveTrue(Pageable pageable);
    public Page<Product> findByCategory(Pageable pageable,String category);
    public Page<Product> findByIsactiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch1, Pageable pageable);

}
