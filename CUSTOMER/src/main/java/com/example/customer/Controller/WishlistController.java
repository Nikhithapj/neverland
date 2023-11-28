package com.example.customer.Controller;

import com.example.library.exceptions.DuplicateWishlistNameException;
import com.example.library.model.Customer;
import com.example.library.model.Wishlist;
import com.example.library.service.CustomerService;
import com.example.library.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class WishlistController {
 private CustomerService customerService;
 private WishlistService wishlistService;

    public WishlistController(CustomerService customerService, WishlistService wishlistService) {
        this.customerService = customerService;
        this.wishlistService = wishlistService;
    }

    @GetMapping("/wishlist")
    public String getWishList(Principal principal, Model model)
    {
        if(principal==null){
            return "redirect:/login";
        }
        Customer customer=customerService.findByUsername(principal.getName());

        List<Wishlist> wishlists=wishlistService.findAllByCustomer(customer);
        if(wishlists.isEmpty()){
            model.addAttribute("check","you dont have any items in your wishlist");
        }
        model.addAttribute("wishlists",wishlists);
        return "wishlist";
    }

    @PostMapping("/createWishlist")
    public String createWishlist(@RequestParam("wishlistName")String wishlistName,
                                 Principal principal, RedirectAttributes attributes){
        if(principal==null){
            return "redirect:/login";
        }else{
            Customer customer=customerService.findByUsername(principal.getName());
            try{
                wishlistService.createWishlist(wishlistName,customer);
            }
            catch(DuplicateWishlistNameException e){
                attributes.addFlashAttribute("errorMessage",e.getMessage());
            }

        }
    return "redirect:/wishlist";//redirect to homepage or wishlist page
 }


 @GetMapping("/add-wishlist/{id}")
    public String addToWishlist(Principal principal, @PathVariable("id")long id, RedirectAttributes redirectAttributes, HttpServletRequest request)
 {
     if(principal==null){
         return "redirect:/login";
     }

     Customer customer=customerService.findByUsername(principal.getName());
     Wishlist wishlist=wishlistService.findById(id);
     boolean exists=wishlistService.findByProductId(id,customer);
     if(exists){
         redirectAttributes.addFlashAttribute("error","product already exists in wishlist");
         return "redirect:"+request.getHeader("Referer");
     }
     wishlistService.save(id,customer);
     return "redirect:/wishlist";
 }

@GetMapping("/delete-wishlist/{id}")
    public String delete(@PathVariable("id")long wishlistId,RedirectAttributes redirectAttributes){
        wishlistService.deleteWishlist(wishlistId);
        redirectAttributes.addFlashAttribute("success","Removed Successfully");
        return "redirect:/wishlist";
}




}
