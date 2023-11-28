package com.example.customer.Controller;

import com.example.library.dto.ProductDto;
import com.example.library.model.Category;
import com.example.library.model.Product;
import com.example.library.service.CategoryService;
import com.example.library.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;


    @GetMapping("/product-list")
    public String productList(Model model) {
        model.addAttribute("page", "Products");
        model.addAttribute("title", "Menu");
        List<Category> categories = categoryService.findAllByActivatedTrue();
        List<ProductDto> products = productService.products();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "shop_list";
    }

    @GetMapping("/product-detail/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        ProductDto product =productService.getById(id);
//        List<ProductDto> productDtoList = productService.findAllByCategory(product.getCategory().getName());
        model.addAttribute("product", product);
        model.addAttribute("title", "Product Detail");
        model.addAttribute("page", "Product Detail");
        model.addAttribute("productDetail", product);
        return "product-detail";
    }




    @GetMapping("/byCategory/{id}")
    public String byCategoryView(@PathVariable("id") Long id,
                                 Model model, Principal principal)
    {
        System.out.println("category id="+id);
        List<ProductDto> productList = productService.findByCategory(id);
        List<Category> categories = categoryService.findAllByActivatedTrue();
        model.addAttribute("page", "Products");
        model.addAttribute("title", "Menu");
        log.info(productList.toString());
        model.addAttribute("products", productList);
        model.addAttribute("categories", categories);
        return "shop_list";
    }


}
