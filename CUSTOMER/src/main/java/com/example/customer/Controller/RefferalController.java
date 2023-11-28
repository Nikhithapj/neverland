package com.example.customer.Controller;

import com.example.library.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RefferalController {
    private final CustomerService customerService;

    public RefferalController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/shareReferral")
    public ResponseEntity<String>shareReferral(@RequestParam String referralCode,
                                               @RequestParam String emailAddress){

    String success="success";
    String result=customerService.shareRefferalCode(referralCode,emailAddress);
    if(result.equals(success)){
        return ResponseEntity.ok("success");
    }
    else{
        return  ResponseEntity.ok("invalid");
    }


}


}
