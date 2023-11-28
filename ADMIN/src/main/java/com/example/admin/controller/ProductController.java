package com.example.admin.controller;

import com.example.library.dto.ProductDto;
import com.example.library.model.Category;
import com.example.library.service.CategoryService;
import com.example.library.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController
{

    private final ProductService productService;
    private final CategoryService categoryService;


    @GetMapping("/products")
    public String products(Model model) {
        List<ProductDto> products = productService.allProduct();
        model.addAttribute("products", products);
        model.addAttribute("size", products.size());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "products";
    }

//    @GetMapping("/products/{pageNo}")
//    public String allProducts(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
//        if (principal == null) {
//            return "redirect:/login";
//        }
//        Page<ProductDto> products = productService.getAllProducts(pageNo);
//        model.addAttribute("title", "Manage Products");
//        model.addAttribute("size", products.getSize());
//        model.addAttribute("products", products);
//        model.addAttribute("currentPage", pageNo);
//        model.addAttribute("totalPages", products.getTotalPages());
//        return "products";
//    }

//    @GetMapping("/search-products/{pageNo}")
//    public String searchProduct(@PathVariable("pageNo") int pageNo,
//                                @RequestParam(value = "keyword") String keyword,
//                                Model model
//    ) {
//        Page<ProductDto> products = productService.searchProducts(pageNo, keyword);
//        model.addAttribute("title", "Result Search Products");
//        model.addAttribute("size", products.getSize());
//        model.addAttribute("products", products);
//        model.addAttribute("currentPage", pageNo);
//        model.addAttribute("totalPages", products.getTotalPages());
//        return "product-result";
//
//    }

    @GetMapping("/add-product")
    public String addProductPage(Model model)
    {

        model.addAttribute("title", "Add Product");
        List<Category> categories = categoryService.findAllByActivatedTrue();
        model.addAttribute("categories", categories);
        model.addAttribute("productDto", new ProductDto());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "add-product";
    }
//   code earlier
    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("productDto") ProductDto product,
                              @RequestParam("imageProduct") List<MultipartFile> imageProduct,
                              RedirectAttributes redirectAttributes) {
        try {
            productService.save(imageProduct, product);
            redirectAttributes.addFlashAttribute("success", "Add new product successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add new product!");
        }
        return "redirect:/products";
    }

// code with binding result and showing backend validation
//    @PostMapping("/save-product")
//    public String saveProduct(@Valid @ModelAttribute("productDto") ProductDto product,
//                              @RequestParam("imageProduct") List<MultipartFile> imageProduct,
//                              BindingResult result, RedirectAttributes redirectAttributes,Model model) {
//        System.out.println("called");
//        if (result.hasErrors()) {
//            System.out.println(result.hasErrors());
//            // Validation errors occurred. You can handle them here.
//            // For example, you can add error messages to the flash attributes.
//            model.addAttribute("error",result.hasErrors());
////            redirectAttributes.addFlashAttribute("error", "Failed to add new product due to validation errors!");
//            return "add-product"; // Redirect back to the form
//        }
//
//        try {
//            // If there are no validation errors, proceed with saving the product.
//            productService.save(imageProduct, product);
//            redirectAttributes.addFlashAttribute("success", "Add new product successfully!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error", "Failed to add new product!");
//        }
//        return "redirect:/products";
//    }

    @GetMapping("/update-product/{id}")
    public String updateProductForm(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        List<Category> categories = categoryService.findAllByActivatedTrue();
        ProductDto productDto = productService.getById(id);



        model.addAttribute("title", "Add Product");
        model.addAttribute("categories", categories);
        model.addAttribute("productDto", productDto);
        return "update-product";
    }

    @PostMapping("/update-product/{id}")
    public String updateProduct(@ModelAttribute("productDto") ProductDto productDto,
                                @RequestParam("imageProduct") List<MultipartFile> imageProduct,
                                RedirectAttributes redirectAttributes) {
        try {

            productService.update(imageProduct, productDto);
            redirectAttributes.addFlashAttribute("success", "Update successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
        }
        return "redirect:/products";
    }

    @RequestMapping(value = "/enable-product/{id}", method = {RequestMethod.GET})
    public String enabledProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.enableById(id);
            redirectAttributes.addFlashAttribute("success", "Enabled successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Enabled failed!");
        }
        return "redirect:/products";
    }

    @RequestMapping(value = "/delete-product/{id}", method = {RequestMethod.GET})
    public String deletedProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        System.out.println(id);
        try {
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Deleted failed!");
        }
        return "redirect:/products";
    }

//    @RequestMapping(value = "/hard-delete-product/{id}", method = RequestMethod.GET)
//    public String hardDeleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
//        try {
//            productService.hardDeleteById(id); // Hard delete the product from the database
//            redirectAttributes.addFlashAttribute("success", "Permanently deleted successfully!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error", "Permanent delete failed!");
//        }
//        return "redirect:/products/0";
//    }

}