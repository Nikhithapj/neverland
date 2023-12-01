package com.example.customer.Controller;

import com.example.library.dto.AddressDto;
import com.example.library.dto.CouponDto;
import com.example.library.model.*;
import com.example.library.service.*;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class OrderController {

private  static final Logger logger= Logger.getLogger(OrderController.class.getName());
private final CustomerService customerService;
private final OrderService orderService;
private  final ShoppingCartService shoppingCartService;
private final WalletService walletService;
private final CouponService couponService;
    public OrderController(CustomerService customerService,CouponService couponService,OrderService orderService,ShoppingCartService shoppingCartService,WalletService walletService) {
        this.shoppingCartService=shoppingCartService;
        this.customerService = customerService;
        this.orderService=orderService;
        this.walletService= walletService;
        this.couponService =couponService;

    }

    @GetMapping("/order")
    public String order(Principal principal, Model model){

    if(principal==null){
        return"redirect:/login";
    }
    String username=principal.getName();
    Customer customer=customerService.findByUsername(username);
    List<Order> orderList=customer.getOrders();
    model.addAttribute("orders",orderList);
    return "order";
}


@GetMapping("/shop-checkout")
public String checkout(Model model, Principal principal, RedirectAttributes redirectAttributes
                       ,HttpServletRequest httpServletRequest){

        if(principal==null)
        {
            return "redirect:/login";
        }
        if(redirectAttributes.getFlashAttributes().containsKey("errorMessage"))
        {
            String errorMessage=(String)redirectAttributes.getFlashAttributes().get("errorMessage");
            model.addAttribute("errorMessage",errorMessage);
        }

    AddressDto addressDto=new AddressDto();
    model.addAttribute("addressDto",addressDto);
    String username=principal.getName();
    Customer customer=customerService.findByUsername(username);
    ShoppingCart shoppingCart=customer.getCart();
    if(shoppingCart==null){
        return "you dont have items in cart ";
    }

    if(customer.getAddress().isEmpty()){
        redirectAttributes.addFlashAttribute("redirectToCheckout", true);
        redirectAttributes.addFlashAttribute("customer", customer);
        model.addAttribute("customer",customer);
        model.addAttribute("error","You must fill the information before checkout!");
        return "checkoutadd-address";
    }else {
        Address address = new Address();
        model.addAttribute("addressEnter", address);
        model.addAttribute("addresses",customer.getAddress());
        model.addAttribute("customer",customer);

        ShoppingCart cart=customer.getCart();
        Wallet wallet=walletService.findByCustomer(customer);
        model.addAttribute("wallet",wallet);
        model.addAttribute("cart",cart);
        List<CouponDto>couponDto=couponService.getAllCoupons();
        model.addAttribute("coupons",couponDto);
    }

    HttpSession httpSession1=httpServletRequest.getSession();
    String name=null;
    if(httpSession1!=null){
        name=httpServletRequest.getRemoteUser();

    }

    model.addAttribute("name",name);
    return "shop-checkout2";
}




@RequestMapping(value="/add-order", method = RequestMethod.POST)
@ResponseBody
public String createOrder(@RequestBody Map<String, Object> data,
                          Principal principal, HttpSession session, Model model)throws RazorpayException,RazorpayException
{
    String paymentMethod = data.get("payment_Method").toString();
    Long address_id = Long.parseLong(data.get("addressId").toString());
    Double oldTotalPrice = (Double) session.getAttribute("totalPrice");
    System.out.println(principal.getName());
    Customer customer = customerService.findByUsername(principal.getName());
    ShoppingCart cart = customer.getCart();

    if (paymentMethod.equals("COD")) {
        Order order = orderService.save(cart, address_id, paymentMethod,oldTotalPrice);
        session.removeAttribute("totalItems");
        session.removeAttribute("totalPrice");
        session.setAttribute("orderId", order.getId());
        model.addAttribute("order", order);
        model.addAttribute("page", "Order Detail");
        model.addAttribute("success", "Order Added Successfully");
        JSONObject options = new JSONObject();
        options.put("status", "Cash");
        return options.toString();

    } else if(paymentMethod.equals("Wallet")){
        WalletHistory walletHistory=walletService.debit(customer.getWallet(),cart.getTotalPrice());
        Order order=orderService.save(cart,address_id,paymentMethod,oldTotalPrice);
        walletService.saveOrderId(order,walletHistory);
        session.removeAttribute("totalItems");
        session.removeAttribute("totalPrice");
        session.setAttribute("orderId",order.getId());
        model.addAttribute("order",order);
        model.addAttribute("page","Order Detail");
        model.addAttribute("success","order added successfully");
        JSONObject options=new JSONObject();
        options.put("status","wallet");
        return options.toString();
    }
else
{
        Order order=orderService.save(cart,address_id,paymentMethod, oldTotalPrice);
        String orderId=order.getId().toString();
        session.removeAttribute("totalItems");
        session.removeAttribute("totalPrice");
        session.setAttribute("orderId",order.getId());
        model.addAttribute("order",order);
        model.addAttribute("page","Order Detail");
        model.addAttribute("success","Order Added SuccessFully");
        RazorpayClient  razorpayClient=new RazorpayClient("rzp_test_k2dDEcGtnGamas","CsqSlj3pnnko2FdSe47WQ7n9");
        JSONObject options=new JSONObject();
        options.put("amount",order.getTotalPrice()*100);
        options.put("currency","INR");
        options.put("receipt",orderId);
        com.razorpay.Order orderRazorpay=razorpayClient.orders.create(options);
        return orderRazorpay.toString();

    }

}


@RequestMapping(value="/Verify-payment",method=RequestMethod.POST)
@ResponseBody
public String verifyPayment(@RequestBody Map<String,Object>data,HttpSession session,Principal principal)throws RazorpayException{
        String secret="CsqSlj3pnnko2FdSe47WQ7n9";
        String order_id=data.get("razorpay_order_id").toString();
        String payment_id=data.get("razorpay_payment_id").toString();
        String signature=data.get("razorpay_signature").toString();
        JSONObject options=new JSONObject();
        options.put("razorpay_order_id",order_id);
        options.put("razorpay_payment_id",payment_id);
        options.put("razorpay_signature",signature);

        boolean status= Utils.verifyPaymentSignature(options,secret);
        Order order=orderService.findOrderById((Long)session.getAttribute("orderId"));
        if(status){
            orderService.updatePayment(order,status);
            Customer customer=customerService.findByUsername(principal.getName());
            ShoppingCart cart =customer.getCart();
            shoppingCartService.deleteCartById(cart.getId());
       }else{
            orderService.updatePayment(order,status);
       }
        JSONObject response=new JSONObject();
        response.put("status",status);
        return response.toString();
}


@RequestMapping(value="/check-out/apply-coupon",method=RequestMethod.POST,params="action=apply")
public String applyCoupon(@RequestParam("couponCode")String couponCode,
                          Principal principal,
                          RedirectAttributes attributes,HttpSession session) {


     if(couponService.findByCouponCode(couponCode.toUpperCase())){
         Coupon coupon=couponService.findByCode(couponCode.toUpperCase());
         ShoppingCart cart=customerService.findByUsername(principal.getName()).getCart();
         Double totalPrice=cart.getTotalPrice();

         if(coupon.getMinOrderAmount()>totalPrice){
             System.out.println(totalPrice);
             String message="Minimum order amount to apply the coupon"+coupon.getCode()+"is"+coupon.getMinOrderAmount();
             System.out.println(message);
             attributes.addFlashAttribute("minOrderAmount",message);
             return "redirect:/shop-checkout";
         }
         session.setAttribute("totalPrice",totalPrice);
         Double newTotalPrice=couponService.applyCoupon(couponCode.toUpperCase(),totalPrice);
         shoppingCartService.updateTotalPrice(newTotalPrice, principal.getName());
         attributes.addFlashAttribute("success","coupon applied successfully");
         attributes.addFlashAttribute("couponCode",couponCode);
         attributes.addFlashAttribute("couponOff",coupon.getOffPercentage());


     }else{
         attributes.addFlashAttribute("error","Coupon code invalid");

     }

     return "redirect:/shop-checkout";
}


@RequestMapping(value="/check-out/remove-coupon",method=RequestMethod.POST,params = "action=remove")

public String  removeCoupon(Principal principal,RedirectAttributes attributes,HttpSession session){
        Double totalPrice=(Double)session.getAttribute("totalPrice");
        shoppingCartService.updateTotalPrice(totalPrice, principal.getName());
        attributes.addFlashAttribute("success","Coupon removed successfully");

        return "redirect:/shop-checkout";
}




@GetMapping("/order-confirmation")
    public String getOrderConfirmation(Model model,HttpSession session){
        if(session.getAttribute("orderId")==null){
            return"redirect:/index";
        }
        Long order_id=(Long)session.getAttribute("orderId");
        Order order=orderService.findOrderById(order_id);
        String paymentMethod=order.getPaymentMethod();
        if(paymentMethod.equals("COD")){
            model.addAttribute("payment","Pending");
        }else{
            model.addAttribute("payment","Successful");
        }
        model.addAttribute("orders",order);
        model.addAttribute("success","Order Added Successfully");
        session.removeAttribute("orderId");

        return "order";
}
   @GetMapping("/cancel-order/{id}")
    public String cancelOrder(@PathVariable("id")long order_id,RedirectAttributes attributes){
        orderService.cancelOrder(order_id);
        attributes.addFlashAttribute("success","cancel order successfully");
        return "redirect:/account?tab=orders";
   }
}
