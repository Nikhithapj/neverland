package com.example.admin.controller;

import com.example.library.dto.AddressDto;
import com.example.library.dto.CustomerDto;
import com.example.library.model.Address;
import com.example.library.model.Customer;
import com.example.library.model.Order;
import com.example.library.repository.OrderRepository;
import com.example.library.service.AddressService;
import com.example.library.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class AccountController {
    private CustomerService customerService;
    private OrderRepository orderRepository;
    private AddressService addressService;

    public AccountController(CustomerService customerService, OrderRepository orderRepository, AddressService addressService) {
        this.customerService = customerService;
        this.orderRepository = orderRepository;
        this.addressService = addressService;
    }

    @GetMapping("/account")
    public String accountHome(Model model, Principal principal, HttpServletRequest httpServletRequest) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        List<Order> orders = orderRepository.findByCustomer(customer);
        Collections.sort(orders, Collections.reverseOrder(Comparator.comparing(Order::getId)));
        model.addAttribute("order", orders);
        model.addAttribute("customers", customer);
        model.addAttribute("addressess", customer.getAddress());

        HttpSession httpSession1 = httpServletRequest.getSession();
        String name = null;
        if (httpSession1 != null) {
            name = httpServletRequest.getRemoteUser();

        }
        model.addAttribute("name", name);
        return "page_account";

    }

    @GetMapping("/add-address")
    public String addAddress(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }
        AddressDto addressDto = new AddressDto();
        model.addAttribute("addressDto", addressDto);
        return "add-address";

    }


    @PostMapping("/save-address")
    public String saveAddress(Model model, Principal principal, @ModelAttribute("addressdto") AddressDto addressDto,
                              RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Address newAddress = new Address();
        newAddress = addressService.save(addressDto, username);
        model.addAttribute("address", newAddress);
        redirectAttributes.addFlashAttribute("message", "Address added");
        return "redirect:/account";
    }

    @GetMapping("/edit-address/{id}")
    public String editAddress(@PathVariable("id") Long id,
                              HttpServletRequest request, Model model,
                              Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        HttpSession session = request.getSession();
        String previousPageUrl = request.getHeader("Referer");
        session.setAttribute("previousPageUrl", previousPageUrl);
        AddressDto addressDto = addressService.findById(id);
        model.addAttribute("addressDto", addressDto);
        return "edit-address";

    }


    @PostMapping("/update-address/{id}")
    public String updateAddress(@PathVariable("id")Long id,HttpServletRequest request
                                ,Principal principal,
                                @ModelAttribute("addressDto")AddressDto addressDto,
                                  RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        HttpSession session = request.getSession();
        String previousPageUrl = (String) session.getAttribute("previousPageUrl");

        String referer = request.getHeader("Referer");
        Address newAddress = addressService.update(addressDto, id);
        redirectAttributes.addFlashAttribute("message", "Address updated");


        if(previousPageUrl!=null){
            return "redirect:"+previousPageUrl;
        }else{
            return "redirect:/accounts";
        }

    }

    @PostMapping("/add-address-checkout")
    public String AddAddress(@ModelAttribute("addressDto")AddressDto addressDto
                              ,Model model,Principal principal,
                             HttpServletRequest request){
        model.addAttribute("address",addressDto);
        addressService.save(addressDto, principal.getName());
        model.addAttribute("success","Address added");
        return "redirect:"+request.getHeader("Referer");
    }

   @RequestMapping(value="/update-info",method={RequestMethod.GET,RequestMethod.PUT})
public String updateCustomer(@ModelAttribute("customer")Customer customer,
                             Model model,RedirectAttributes redirectAttributes,
                             Principal principal){
       if (principal == null) {
           return "redirect:/login";
       }
       Customer customerSaved=customerService.saveInfo(customer,new Address());
       redirectAttributes.addFlashAttribute("customer",customerSaved);
       return "redirect:/account";
    }

//    @GetMapping("/contact")
//    public String getContactUs(Model model) {
//        List<Category> categories = categoryService.findAllByActivated();
//        model.addAttribute("categories", categories);
//        return "contact";
//    }


@GetMapping("/add-profile")
    public String addprofile(Model model,Principal principal){
    if (principal == null) {
        return "redirect:/login";
    }
    String username= principal.getName();
    Customer customer=customerService.findByUsername(username);
    CustomerDto customerDto=new CustomerDto();
    model.addAttribute("customerDto",customerDto);
    return "profile";
}

@PostMapping("/checkRefferalCode")
    public ResponseEntity<String>checkReferralCode(@RequestParam String referralCode){
        if(referralCode==null||referralCode.trim().isEmpty()){
            return ResponseEntity.badRequest().body("Referral code is required");
        }
        Customer customer=customerService.findByReferralCode(referralCode);
        if(customer!=null)
        {
            return  ResponseEntity.ok("valid");
        }else {
            return ResponseEntity.ok("invalid");
        }
}


}






































