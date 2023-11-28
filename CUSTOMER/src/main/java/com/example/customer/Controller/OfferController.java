//package com.example.customer.Controller;
//
//import com.example.library.dto.OfferDto;
//import com.example.library.dto.ProductDto;
//import com.example.library.model.Category;
//import com.example.library.service.CategoryService;
//import com.example.library.service.OfferService;
//import com.example.library.service.ProductService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.security.Principal;
//import java.util.List;
//
//@Controller
//public class OfferController {
//    private CategoryService categoryService;
//    private OfferService offerService;
//    private ProductService productService;
//
//    public OfferController(OfferService offerService,ProductService productService,CategoryService categoryService) {
//        this.offerService = offerService;
//        this.productService=productService;
//        this.categoryService =categoryService;
//    }
//
//    @GetMapping("/offers")
//    public String getCoupon(Principal principal, Model model){
//        if(principal==null){
//            return "redirect:/login";
//        }
//        List<OfferDto>offerDtoList=offerService.getAllOffers();
//        model.addAttribute("offers",offerDtoList);
//        model.addAttribute("size",offerDtoList.size());
//        return "offers";
//    }
//
//    @GetMapping("/offers/add-offer")
//    public String getAddOffer(Principal principal,Model model){
//        if(principal == null){
//            return "redirect:/login";
//        }
//        List<ProductDto>productList=productService.findAllProducts();
//        List<Category>categoryList=categoryService.findAllByActivatedTrue();
//        model.addAttribute("products",productList);
//        model.addAttribute("categories",categoryList);
//        model.addAttribute("offer",new OfferDto());
//        return "add-offer";
//    }
//
//
//@PostMapping("/offers/save")
//    public String addOffer(@ModelAttribute("offer")OfferDto offer, RedirectAttributes redirectAttributes){
//        try{
//            offerService.save(offer);
//            redirectAttributes.addFlashAttribute("success","Added new offers successfully");
//        }
//        catch(Exception e){
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error","failed to add new offer");
//        }
//        return "redirect:/offers";
//}
//
//
//@GetMapping("/offers/update-offer/{id}")
//    public String updateCoupon(@ModelAttribute("offer")OfferDto offerDto,RedirectAttributes redirectAttributes) {
//    try {
//        offerService.update(offerDto);
//        redirectAttributes.addFlashAttribute("success","updated successfully");
//    }catch(Exception e){
//        e.printStackTrace();
//        redirectAttributes.addFlashAttribute("error","Error server, please try again!");
//    }
//return "redirect:/offers";
//}
//
//
//@GetMapping("/disable-offer/{id}")
//    public String  disable(@PathVariable("id")long id,RedirectAttributes redirectAttributes){
//        offerService.disable(id);
//        redirectAttributes.addFlashAttribute("success","offer disabled");
//    return "redirect:/offers";
//
//}
//
//@GetMapping("/enable-offer/{id}")
//    public String enable(@PathVariable("id")long id,RedirectAttributes redirectAttributes){
//        offerService.enable(id);
//        redirectAttributes.addFlashAttribute("success","offer enabled");
//    return "redirect:/offers";
//
//}
//
//
//@GetMapping("/delete-offer/{id}")
//    public String delete(@PathVariable ("id")long  id,RedirectAttributes redirectAttributes){
//        offerService.deleteOffer(id);
//        redirectAttributes.addFlashAttribute("success","offer deleted");
//    return "redirect:/offers";
//
//}
//
//
//
//
//
//}
