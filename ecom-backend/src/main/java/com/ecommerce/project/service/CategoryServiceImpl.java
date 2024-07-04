package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        //Sort is an in-built class used to sort things
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //Pageable is an interface provided by SpringDataJPA and PageRequest is an implemention of Pageable Interface.
        //Pageable pageDetails = PageRequest.of(pageNumber, pageSize);
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();

        //Check if any category id present in the database or not
        if (categoryRepository.count() == 0)
            throw new APIException("No categories available in the database.");
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        // Setting Paginaation Metadata for frontend application to consume for rendering
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createNewCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        //Extract the categoryName of new category and check if it is already available in the database.
        Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());
        //Check if new category's name already exists in the database or not. If yes, throw the Exception.
        if(categoryFromDb != null)
            throw new APIException("Category with the name '"+ category.getCategoryName() + "' already exists!!!");
        Category savedCategory = categoryRepository.save(category);
        CategoryDTO savedCategoryDTO = modelMapper.map(savedCategory, CategoryDTO.class);
        return savedCategoryDTO;
    }

// Following is the implementation of createNewCategory() without DTO pattern
//    @Override
//    public void createNewCategory(Category category) {
//        //Extract the categoryName of new category and check if it is already available in the database.
//        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
//        //Check if new category's name already exists in the database or not. If yes, throw the Exception.
//        if(savedCategory != null)
//            throw new APIException("Category with the name '"+ category.getCategoryName() + "' already exists!!!");
//        categoryRepository.save(category);
//    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category","CategoryId",categoryId));
        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

//    @Override
//    public String deleteCategory(Long categoryId) {
//        Category category = categoryRepository.findById(categoryId)
//                .orElseThrow( () -> new ResourceNotFoundException("Category","CategoryId",categoryId));
//
//        categoryRepository.delete(category);
//        return "Category with CategoryId : "+ categoryId + " is deleted Successfully";
//    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category","CategoryId",categoryId));
        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }


//    @Override
//    public Category updateCategory(Category category, Long categoryId) {
//
//        Category savedCategory = categoryRepository.findById(categoryId)
//                .orElseThrow( () -> new ResourceNotFoundException("Category","CategoryId",categoryId));
//        category.setCategoryId(categoryId);
//        savedCategory = categoryRepository.save(category);
//        return savedCategory;
//    }

}
