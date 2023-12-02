package com.example.admin.controller;

import com.example.library.dto.CustomerDto;
import com.example.library.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
//@RequiredArgsConstructor
public class CustomerController {
    private  CustomerService  customerService;

   private HttpSession httpSession;



    public CustomerController(CustomerService customerService)
    {

        this.customerService = customerService;
    }

    @GetMapping("/users")
    public String listUser(Model model, Principal principal) {
        if (principal == null) {
            return "login";
        }
        List<CustomerDto> customers = new ArrayList<>();
        customers = customerService.findAll();
        model.addAttribute("title", "users");
        model.addAttribute("users", customers);
        model.addAttribute("size", customers.size());
        return "users";
    }



//    @GetMapping("/user_home")
//    public  String viewUserHome(@AuthenticationPrincipal CustomerDetails customer, Model model  ){
//        model.addAttribute("customer",customer);
//        return "user_home";
//    }
//    public String blockUser(@PathVariable("id") Long id) {
//        try{
//            customerService.blockUser(id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "redirect:/users";
//    }

    @RequestMapping(value="/unblock-users/{id}", method={RequestMethod.GET,RequestMethod.POST})
    public String unblockUser(@PathVariable("id") Long id, RedirectAttributes attributes){
        try {
//          customerService.blockUser(id);

            customerService.unblockById(id);
            attributes.addFlashAttribute("success", "Unblocked successfully");
        }catch (Exception e){
            e.printStackTrace();
            attributes.addFlashAttribute("error","Failed to Unblock");
        }
        return "redirect:/users";
    }

//correct code
    @RequestMapping(value="/block-users/{id}", method={RequestMethod.GET,RequestMethod.PUT})
    public String blockUser(@PathVariable("id") Long id, RedirectAttributes attributes){

        try {


//            customerService.blockUser(id);
            customerService.blockById(id);


            System.out.println("1");
            attributes.addFlashAttribute("success", "Blocked successfully");
        }catch (Exception e){
            e.printStackTrace();
            attributes.addFlashAttribute("error","Failed to delete");
        }
        System.out.println("2");
        return "redirect:/users";
    }



}