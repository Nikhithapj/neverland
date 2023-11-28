package com.example.library.service.Impl;

import com.example.library.model.Category;
import com.example.library.dto.CategoryDto;
import com.example.library.model.Category;
import com.example.library.repository.CategoryRepository;
import com.example.library.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category save(Category category) {
        Category categorySave = new Category(category.getName());
        return categoryRepository.save(categorySave);

    }

    @Override
    public Category update(Category category) {
        Category categoryUpdate = categoryRepository.getById(category.getId());
        categoryUpdate.setName(category.getName());
        return categoryRepository.save(categoryUpdate);
    }

    @Override
    public List<Category> findAllByActivatedTrue() {
        return categoryRepository.findAllByActivatedTrue();
    }

    @Override
    public List<Category> findALl() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.getById(id);
        category.setActivated(false);
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public void enableById(Long id) {
        Category category = categoryRepository.getById(id);
        category.setActivated(true);
        category.setDeleted(false);
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryDto> getCategoriesAndSize() {
        List<CategoryDto> categories = categoryRepository.getCategoriesAndSize();
        return categories;
    }

}