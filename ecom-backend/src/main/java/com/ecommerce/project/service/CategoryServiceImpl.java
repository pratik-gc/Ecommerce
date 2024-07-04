package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        //Check if any category id present in the database or not
        if (categoryRepository.count() == 0)
            throw new APIException("No categories available in the database.");
        return categoryRepository.findAll();
    }

    @Override
    public void createNewCategory(Category category) {
        //Extract the categoryName of new category and check if it is already available in the database.
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        //Check if new category's name already exists in the database or not. If yes, throw the Exception.
        if(savedCategory != null)
            throw new APIException("Category with the name '"+ category.getCategoryName() + "' already exists!!!");
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category","CategoryId",categoryId));

        categoryRepository.delete(category);
        return "Category with CategoryId : "+ categoryId + " is deleted Successfully";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {

        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category","CategoryId",categoryId));
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return savedCategory;
    }

}
