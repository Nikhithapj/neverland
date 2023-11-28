package com.example.customer.Controller;

import com.example.library.dto.WalletHistoryDto;
import com.example.library.model.Customer;
import com.example.library.model.Wallet;
import com.example.library.model.WalletHistory;
import com.example.library.service.CustomerService;
import com.example.library.service.WalletService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
@Controller

public class WalletController
{

  private CustomerService customerService;
  private WalletService walletService;

    public WalletController(CustomerService customerService, WalletService walletService) {
        this.customerService = customerService;
        this.walletService = walletService;
    }

    @GetMapping("/wallets")
    public String getWalletTab(Principal principal, Model model){
        System.out.println("hi");
        if(principal==null){
            return "redirect:/login";
        }
        Customer customer=customerService.findByUsername(principal.getName());
        Wallet wallet = walletService.findByCustomer(customer);
        List<WalletHistoryDto>walletHistoryDtoList=walletService.findAllById(wallet.getId());
        model.addAttribute("walletHistoryList",walletHistoryDtoList);
        model.addAttribute("wallet",wallet);
        return "wallet";

    }



    @RequestMapping(value="/add-wallet",method= RequestMethod.POST)
    @ResponseBody
    public String addWallet(@RequestBody Map<String ,Object>data, Principal principal,
                            HttpSession session, Model model)throws RazorpayException{
        if(principal==null){
            return "redirect:/login";
        }

        Customer customer=customerService.findByUsername(principal.getName());

        double amount=Double.parseDouble(data.get("amount").toString());
        WalletHistory walletHistory=walletService.save(amount,customer);
        String walletHistoryId=walletHistory.getId().toString();
        session.setAttribute("walletHistoryId",walletHistory.getId());
        model.addAttribute("success","Money added successfully");
        RazorpayClient razorpayClient=new RazorpayClient("rzp_test_k2dDEcGtnGamas","CsqSlj3pnnko2FdSe47WQ7n9");

        JSONObject options=new JSONObject();
        options.put("amount",amount*100);
        options.put("currency","INR");
        options.put("receipt",walletHistoryId);
        com.razorpay.Order orderRazorpay=razorpayClient.orders.create(options);
        return orderRazorpay.toString();
    }


    @RequestMapping(value="/verify-wallet",method=RequestMethod.POST)
    @ResponseBody
    public String verifyWalletPayment(@RequestBody Map<String ,Object>data,HttpSession session,
                                      Principal principal)
            throws RazorpayException{
        String secret="CsqSlj3pnnko2FdSe47WQ7n9";
        String order_id=data.get("razorpay_order_id").toString();
        String payment_id=data.get("razorpay_payment_id").toString();
        String signature=data.get("razorpay_signature").toString();
        JSONObject options=new JSONObject();
        options.put("razorpay_order_id",order_id);
        options.put("razorpay_payment_id",payment_id);
        options.put("razorpay_signature",signature);

        boolean status= Utils.verifyPaymentSignature(options,secret);
        WalletHistory walletHistory=walletService.findById((Long)session.getAttribute("walletHistoryId"));
        Customer customer=customerService.findByUsername(principal.getName());
        if(status){
            walletService.updateWallet(walletHistory,customer,status);

        }else
        {
            walletService.updateWallet(walletHistory,customer,status);

        }
        JSONObject response=new JSONObject();
        response.put("status",status);
        session.removeAttribute("walletHistoryId");
        return response.toString();
  }



}
