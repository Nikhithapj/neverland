package com.example.customer.Controller;

import com.example.library.dto.ProductDto;
import com.example.library.model.Customer;
import com.example.library.model.Product;
import com.example.library.model.ShoppingCart;
import com.example.library.service.CustomerService;
import com.example.library.service.ProductService;
import com.example.library.service.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class ShoppingCartController {



    public ShoppingCartController(CustomerService customerService, ProductService productService, ShoppingCartService shoppingCartService) {
        this.customerService = customerService;
        this.productService = productService;
        this.shoppingCartService=shoppingCartService;
    }
    private ShoppingCartService shoppingCartService;
    private CustomerService customerService;
    private ProductService productService;

    @GetMapping("/cart")
    public String getCart(Model model, Principal principal){
        if(principal==null){
            return "redirect:/login";
        }
        else{
            Customer customer=customerService.findByUsername(principal.getName());
            ShoppingCart cart=customer.getCart();
            if (cart==null){
                model.addAttribute("check",
                        "you dont have items in your cart" );

            }
            if(cart!=null){
                model.addAttribute("grandTotal",cart.getTotalPrice());
            }
            model.addAttribute("shoppingCart",cart);
            model.addAttribute("title","cart");

            return "cart";
        }
    }

    @PostMapping("/add-to-cart")
    public String addItemToCart(@RequestParam("productId")Long id,
                                @RequestParam(name = "quantity",defaultValue = "1")int quantity,
//                                @RequestParam(value="selectSizeId",defaultValue="2")long sizeId,
                                HttpServletRequest request,
                                Model model,
                                Principal principal,
                                HttpSession session){

        ProductDto productDto=productService.getById(id);
        if(principal==null){
            return "redirect:/login";
        }else {
            String username = principal.getName();
            ShoppingCart shoppingCart = shoppingCartService.addItemToCart(productDto, quantity, username);
            session.setAttribute("totalItems", shoppingCart.getTotalItems());
            model.addAttribute("shoppingCart", shoppingCart);
        }
            return "redirect:"+request.getHeader("Referer");
        }

@RequestMapping(value="/update-cart",method= RequestMethod.POST,params="action=update")
public String updateCart(@RequestParam("id")long id,
                         @RequestParam("cart_item_id")Long cart_item_id,
                         @RequestParam("quantity")int quantity,
//                         @RequestParam(value="size",required = false,defaultValue="0")long sizeId,
                         Model model,
                         Principal principal) {
    if (principal == null) {
        return "redirect:/login";
    } else {
        ProductDto productDto = productService.findById(id);
        String username = principal.getName();
        ShoppingCart shoppingCart = shoppingCartService.updateCart(productDto, quantity, username, cart_item_id);
        model.addAttribute("shoppingCart", shoppingCart);


        return "redirect:/cart";
    }

}


@RequestMapping(value="/update-cart",method=RequestMethod.POST,params="action=delete")
public String deleteItem(@RequestParam("id")long id,
                         Model model,
                         Principal principal){
    if(principal==null){
        return "redirect:/login";
    }
    else{
        ProductDto productDto=productService.findById(id);
        String username=principal.getName();
        ShoppingCart shoppingCart=shoppingCartService.removeItemFromCart(productDto,username);
        model.addAttribute("shoppingCart",shoppingCart);
        return "redirect:/cart";
    }


}
}
