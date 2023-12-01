package com.example.customer.Controller;

import com.example.customer.config.CustomerDetails;
import com.example.library.dto.AddressDto;
import com.example.library.dto.CustomerDto;
import com.example.library.model.Address;
import com.example.library.model.Customer;
import com.example.library.model.Order;
import com.example.library.model.PasswordDTO;
import com.example.library.repository.OrderRepository;
import com.example.library.service.AddressService;
import com.example.library.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private BCryptPasswordEncoder passwordEncoder;

    public AccountController(CustomerService customerService, OrderRepository orderRepository, AddressService addressService,BCryptPasswordEncoder passwordEncoder) {
        this.customerService = customerService;
        this.orderRepository = orderRepository;
        this.addressService = addressService;
        this.passwordEncoder=passwordEncoder;
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
        model.addAttribute("orders", orders);
        model.addAttribute("customer", customer);
        model.addAttribute("addresses", customer.getAddress());

        HttpSession httpSession1 = httpServletRequest.getSession();
        String name = null;
        if (httpSession1 != null) {
            name = httpServletRequest.getRemoteUser();

        }
        model.addAttribute("name", name);
        return "page-account";

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


//    @PostMapping("/save-address")
//    public String saveAddress(Model model, Principal principal, @ModelAttribute("addressdto") AddressDto addressDto,
//                              RedirectAttributes redirectAttributes) {
//        if (principal == null)
//        {
//            return "redirect:/login";
//        }
//        String username = principal.getName();
//        Address newAddress = new Address();
//        newAddress = addressService.save(addressDto, username);
//        model.addAttribute("address", newAddress);
//        redirectAttributes.addFlashAttribute("message", "Address added");
//        return "redirect:/account";
//    }

    @PostMapping("/save-address")
    public String saveAddress(Model model, Principal principal, @ModelAttribute("addressdto") AddressDto addressDto,
                              @RequestParam(value = "checkout", required = false) String checkout,
                              RedirectAttributes redirectAttributes) {
        if (principal == null)
        {
            return "redirect:/login";
        }



        String username = principal.getName();
        Address newAddress = new Address();
        newAddress = addressService.save(addressDto, username);
        if (redirectAttributes.getFlashAttributes().containsKey("redirectToCheckout")) {
            System.out.println("redirectToCheckout: " + redirectAttributes.getFlashAttributes().containsKey("redirectToCheckout"));

            // Add address to customer object or update customer's address
            // Redirect back to the checkout page
            return "redirect:/shop-checkout";
        }
        model.addAttribute("address", newAddress);
        redirectAttributes.addFlashAttribute("message", "Address added");
        if(checkout!=null){
            return "redirect:/shop-checkout";
        }
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




    @GetMapping("/add-profile")
    public String addprofile(Model model,Principal principal){
        if(principal == null)
        {
            return "redirect:/login";
        }
        String username= principal.getName();
        Customer customer=customerService.findByUsername(username);
        CustomerDto customerDto=new CustomerDto();
        model.addAttribute("customerDto",customerDto);
        return "profile";
    }

//    @GetMapping("/updateCustomerDetails/{id}")
//    public String saveUserProfile(@Valid CustomerDto customerDto, BindingResult bindingResult, Model model,Principal principal){
//        if(principal == null)
//        {
//            return "redirect:/login";
//        }
//        System.out.println(customerDto);
//        if(bindingResult.hasErrors()){
//            model.addAttribute("customerDto",customerDto);
//            return "myProfile";
//        }else{
//            //code to update user details...
//            customerService.updateUserProfile(customerDto);
//            return "redirect:/account";
//        }
//    }


    @GetMapping("/edit-profile/{id}")
    public String editProfile(Model model, @PathVariable("id")Long id,Principal principal,HttpServletRequest request)
    {
        if(principal==null)
        {
            return "redirect:/login";
        }
        HttpSession session= request.getSession();
        String previousPageUrl = request.getHeader("referer");
        session.setAttribute("previousPageUrl",previousPageUrl);
        CustomerDto customerDto=customerService.findByIdProfile(id);
        model.addAttribute("customerDto",customerDto);
        return "myProfile";
    }

    @PostMapping("/update-profile/{id}")
    public String updateProfile(@PathVariable("id")Long id,HttpServletRequest request,Principal principal,@ModelAttribute CustomerDto customerDto,RedirectAttributes redirectAttributes)
    {
        if(principal==null)
        {
            return "redirect:/login";
        }
        HttpSession session= request.getSession();
        String previousPageUrl = (String) session.getAttribute("previousPageUrl");

        String referer = request.getHeader("referer");

        Customer newcustomer=customerService.updates(customerDto,id);
        redirectAttributes.addFlashAttribute("message","profile updated");
        if(previousPageUrl != null)
        {
            return "redirect:"+ previousPageUrl;
        }
        else {
            return "redirect:/account";
        }


}
//@GetMapping("/ChangePassword")
//    public String showChangepasswordpage(Authentication authentication,Model model){
//        Customer customer= (Customer) authentication.getPrincipal();
//    PasswordDTO passwordDTO=new PasswordDTO();
//    passwordDTO.setId(customer.getId());
//    model.addAttribute("passwordDTO",passwordDTO);
//    model.addAttribute("mismatch"," ");
//    return "changePassword";
//}
//@PostMapping("/ChangePassword")
//    public String changepassword(@Valid PasswordDTO passwordDTO,BindingResult bindingResult,Model model){
//        if(bindingResult.hasErrors()){
//            model.addAttribute("passwordDTO",passwordDTO);
//            model.addAttribute("mismatch"," ");
//            return "/changePassword";
//        }else if(!passwordDTO.getPassword().equals(passwordDTO.getConfirmPassword())){
//            model.addAttribute("mismatch","password mismatch plz cinfirm");
//            model.addAttribute("passwordDto",passwordDTO);
//            return "/changePassword";
//
//    }
//        else{
//            customerService.changePassword(passwordDTO);
//            model.addAttribute("mismatch","password changed");
//            model.addAttribute("passwordDto",passwordDTO);
//            return "/changePassword";
//        }
//}

    @GetMapping("/ChangePassword")
    public String showChangepasswordpage(Authentication authentication, Model model) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User) {
            // Handle the case where the principal is a User object
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
            // You might want to retrieve additional information from the user, or redirect to an error page.
            // For now, we'll set a default ID.
            PasswordDTO passwordDTO = new PasswordDTO();
            passwordDTO.setId(0L); // Set a default ID
            model.addAttribute("passwordDTO", passwordDTO);
            model.addAttribute("mismatch", " ");
            return "changePassword";
        } else if (principal instanceof com.example.library.model.Customer) {
            // Handle the case where the principal is a Customer object
            com.example.library.model.Customer customer = (com.example.library.model.Customer) principal;
            PasswordDTO passwordDTO = new PasswordDTO();
            passwordDTO.setId(customer.getId());
            model.addAttribute("passwordDTO", passwordDTO);
            model.addAttribute("mismatch", " ");
            return "changePassword";
        } else {
            // Handle the case where the principal is neither User nor Customer
            // You might want to redirect to an error page or handle it appropriately.
            return "errorPage";
        }
    }

    @PostMapping("/ChangePassword")
    public String changepassword(@Valid @ModelAttribute("passwordDTO") PasswordDTO passwordDTO, BindingResult bindingResult, Model model, Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                // Handle validation errors
                model.addAttribute("passwordDTO", passwordDTO);
                model.addAttribute("mismatch", " ");
                return "changePassword";
            } else if (!passwordDTO.getPassword().equals(passwordDTO.getConfirmPassword())) {
                // Handle password mismatch
                model.addAttribute("mismatch", "Password mismatch, please confirm");
                model.addAttribute("passwordDto", passwordDTO);
                return "changePassword";
            } else {
                String username= principal.getName();
                Customer customer=customerService.findByUsername(username);
                long cusId=customer.getId();
                String password=passwordEncoder.encode(passwordDTO.getPassword());
                passwordDTO.setPassword(password);
                System.out.println(passwordDTO);

                customerService.changePassword(cusId,passwordDTO);
                model.addAttribute("mismatch", "Password changed");
                model.addAttribute("passwordDto", passwordDTO);
                return "redirect:/account";
            }
        } catch (Exception e) {
            // Log the exception or handle it as needed
            model.addAttribute("error", "An error occurred while processing your request");
            return "errorPage";
        }
    }





}






































