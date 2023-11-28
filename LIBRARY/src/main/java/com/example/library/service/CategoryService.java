package com.example.library.service;

import com.example.library.dto.CategoryDto;
import com.example.library.model.Category;

import java.util.List;

public interface CategoryService {
    Category save(Category category);

    Category update(Category category);

    List<Category> findAllByActivatedTrue();

    List<Category> findALl();

    Category findById(long id);

    void deleteById(Long id);

    void enableById(Long id);

    List<CategoryDto> getCategoriesAndSize();
}