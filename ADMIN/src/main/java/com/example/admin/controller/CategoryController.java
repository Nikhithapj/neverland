package com.example.admin.controller;


import com.example.library.model.Category;
import com.example.library.model.Product;
import com.example.library.service.CategoryService;
import com.example.library.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/categories")
    public String categories(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Manage Category");
        List<Category> categories = categoryService.findALl();
        model.addAttribute("categories", categories);
        model.addAttribute("size", categories.size());
        model.addAttribute("categoryNew", new Category());
        return "categories";
    }

    @PostMapping("/save-category")
    public String save(@Valid @ModelAttribute("categoryNew") Category category, Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryService.save(category);
            model.addAttribute("categoryNew", category);
            redirectAttributes.addFlashAttribute("success", "Add successfully!");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
        } catch (Exception e2) {
            e2.printStackTrace();
            model.addAttribute("categoryNew", category);
            redirectAttributes.addFlashAttribute("error",
                    "Error server");
        }
        return "redirect:/categories";
    }

    @RequestMapping(value = "/findById/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public Category findById(@PathVariable("id") Long id) {
        return categoryService.findById(id);
    }

    @PostMapping("/update-category")
    public String update(Category category, RedirectAttributes redirectAttributes) {
        try {
            categoryService.update(category);
            redirectAttributes.addFlashAttribute("success", "Update successfully!");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error from server or duplicate name of category, please check again!");
        }
        return "redirect:/categories";
    }

//correct code
    @RequestMapping(value = "/delete-category/{id}", method = {RequestMethod.GET})
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try
        {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server");
        }
        return "redirect:/categories";
    }

    @RequestMapping(value = "/enable-category/{id}", method = {RequestMethod.GET})
    public String enable(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.enableById(id);
            redirectAttributes.addFlashAttribute("success", "Enable successfully");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Err");
        }
        return "redirect:/categories";
    }

//    @RequestMapping(value = "/delete-category/{id}", method = {RequestMethod.GET})
//    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
//        try {
//            // First, retrieve the category by its ID
//            Optional<Category> category = categoryService.findById(id);
//
//            // Check if the category exists
//            if (category != null) {
//                // Retrieve the products associated with the category
//                List<Category> products = categoryService.findAllByActivatedTrue();
//
//                // Delete the products
//                for (Category product : products) {
//                    productService.deleteById(product.getId());
//                }
//
//                // Now, delete the category
//                categoryService.deleteById(id);
//                return "redirect:/categories";
////                redirectAttributes.addFlashAttribute("success", "Category and its products deleted successfully!");
////            } else {
////                redirectAttributes.addFlashAttribute("error", "Category not found!");
////            }
////        } catch (DataIntegrityViolationException e1) {
////            e1.printStackTrace();
////            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
////        }
////        catch (Exception e2) {
////           e2.printStackTrace();
////          redirectAttributes.addFlashAttribute("error", "Error server");
////      }
////                return "redi rect:/categories";
//            }
////        }
////        catch(exception e)
////        {
////            System.out.println(e);
////            e.printStackTrace();
////           redirectAttributes.addFlashAttribute("error", "Error server");
//
////    }
//            return "redirect:/categories";

//        }

}