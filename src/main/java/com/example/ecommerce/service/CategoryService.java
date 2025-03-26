package com.example.ecommerce.service;

import com.example.ecommerce.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {

    public Category saveCategory(Category category);
    public Boolean existCategory(String name);
    public List<Category> getAllCategory();
    public boolean deleteCategory(int id);
    public Category getCategoryById(int id);
    public List<Category> getAllActiveCategory();

}
