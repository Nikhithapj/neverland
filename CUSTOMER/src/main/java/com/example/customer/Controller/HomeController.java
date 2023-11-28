package com.example.customer.Controller;


//import com.example.library.model.Customer;
//import com.example.library.model.ShoppingCart;
import com.example.customer.config.CustomerDetails;
import com.example.library.dto.CustomerDto;
import com.example.library.dto.ProductDto;
import com.example.library.model.Customer;
import com.example.library.repository.CustomerRepository;
import com.example.library.service.CategoryService;
import com.example.library.service.CustomerService;
import com.example.library.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
@Controller
@RequiredArgsConstructor
public class HomeController {
    @Autowired
    private CustomerService customerService;
    private ProductService productService;
    public HomeController(CustomerService customerService, ProductService productService,
                          CategoryService categoryService, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.customerRepository = customerRepository;
    }

    private CategoryService categoryService;
    private  CustomerRepository customerRepository;

    CustomerDetails  customerDetails;
    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String home(Model model, Principal principal, HttpSession session) {
//        if (principal == null) {
//            return "redirect:/login";
//        } else {
//            Customer customer = customerService.findByUsername(principal.getName());
//            session.setAttribute("userLoggedIn", true);
//            session.setAttribute("username", customer.getFirstName() + " " + customer.getLastName());
            model.addAttribute("page", "Products");
            model.addAttribute("title", "Menu");
//            List<ProductDto> products = productService.products();
//            model.addAttribute("products", products);

        return "index";
    }



}
























