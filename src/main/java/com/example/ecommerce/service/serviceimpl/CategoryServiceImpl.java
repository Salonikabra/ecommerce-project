package com.example.ecommerce.service.serviceimpl;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryRepository categoryRepository;
    @Override
    public Category saveCategory(Category category) {




        return categoryRepository.save(category);


    }

    @Override
    public Boolean existCategory(String name) {
       return categoryRepository.existsByName(name);

    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();

    }

    @Override
    public boolean deleteCategory(int id) {
        Category category=categoryRepository.findById(id).orElse(null);
        if(!ObjectUtils.isEmpty(category))
        {
            categoryRepository.delete(category);
            return true;
        }
        return false;
    }

    @Override
    public Category getCategoryById(int id) {
        Category category=categoryRepository.findById(id).orElse(null);
        return category;
    }

    @Override
    public List<Category> getAllActiveCategory() {
        List<Category> categories=categoryRepository.findByIsactiveTrue();

        return categories;
    }

    @Override
    public Page<Category> getAllCategoryPagination(Integer pageNo,Integer pageSize) {
        Pageable pageable=PageRequest.of(pageNo,pageSize);
        return categoryRepository.findAll(pageable);

    }


}
