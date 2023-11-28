package com.example.customer.Controller;



import com.example.library.dto.CustomerDto;
import com.example.library.model.Customer;
import com.example.library.service.CustomerService;
import com.example.library.service.SmsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final CustomerService customerService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SmsService smsService;



    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        model.addAttribute("page", "Home");
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Register");
        model.addAttribute("page", "Register");
        model.addAttribute("customerDto", new CustomerDto());
        return "register";
    }

    @PostMapping("/do-register")
    public String registerCustomer(@Valid @ModelAttribute("customerDto") CustomerDto customerDto,
                                   BindingResult result,
                                   Model model, HttpSession httpSession) {
        try { 
            if (result.hasErrors()) {
                model.addAttribute("customerDto", customerDto);
                return "register";
            }
            String username = customerDto.getUsername();
            Customer customer = customerService.findByUsername(username);
            if (customer != null) {
                model.addAttribute("customerDto", customerDto);
                model.addAttribute("error", "Email has been register!");
                return "register";
            }

            String otp = smsService.generateOtp();
            System.out.println(otp);
            smsService.sendOtp(otp);
            //otp timestamp in session
            httpSession.setAttribute("otpTimestamp", System.currentTimeMillis());

            httpSession.setAttribute("customerDto",customerDto);
            httpSession.setAttribute("otp",otp);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Server is error, try again later!");
        }
        model.addAttribute("otpGenerationTime",System.currentTimeMillis());

        return "otp-verification";
    }
    //original code kids
//    @PostMapping("/do-register/verify")
//    public String verifyOtp(HttpSession session, @RequestParam("inputOtp") String inputOtp, Model model) {
//        try {
//            CustomerDto customerDto = (CustomerDto) session.getAttribute("customerDto");
//            String storedOtp = (String) session.getAttribute("otp");
//
//            if (customerDto != null && storedOtp != null && storedOtp.equals(inputOtp)) {
//                // OTP verified, save user details and login user
//                customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
//                customerService.save(customerDto);
//
//                // Clear session attributes
//                session.removeAttribute("customerDto");
//                session.removeAttribute("otp");
//
//                return "redirect:/login";
//            } else {
//                model.addAttribute("error", "OTP is not valid");
//                return "otp-verification";
//            }
//        } catch (Exception e) {
//            e.printStackTrace(); // Handle exceptions appropriately
//            model.addAttribute("error", "Server error. Please try again later.");
//            return "otp-verification";
//        }
//    }

//original code do register verify 8 9
//    @PostMapping("/do-register/verify")
//    public String verifyOtp(HttpSession session,
//                            @RequestParam("inputOtp")String inputOtp, Model model)
//    {
//
//        CustomerDto customerDto = (CustomerDto) session.getAttribute("customerDto");
//        String otp = (String) session.getAttribute("otp");
//
//        if (customerDto != null && otp.equals(inputOtp)) {
//            // OTP verified, save user details and login user
//            customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
//            customerService.save(customerDto);
//
//        } else {
//            model.addAttribute("error","OTP is not valid");
//
//            return "otp-verification";
//        }
//
//        session.removeAttribute("customerDto");
//        session.removeAttribute("otp-verification");
//
//
//        return "redirect:/login";
//    }



    @PostMapping("/do-register/verify")
    public String verifyOtp(HttpSession session,
                            @RequestParam("inputOtp") String inputOtp, Model model) {

        CustomerDto customerDto = (CustomerDto) session.getAttribute("customerDto");
        String otp = (String) session.getAttribute("otp");
        Long otpTimestamp = (Long) session.getAttribute("otpTimestamp");
        System.out.println(otpTimestamp);



        // Check if the OTP and customerDto are present in the session
        if (customerDto != null && otp != null && otpTimestamp != null) {
            // Check if the OTP is correct
            if (otp.equals(inputOtp)) {
                // Check if OTP is still valid (within 5 minutes in this example)
                long currentTime = System.currentTimeMillis();
                long otpTime = otpTimestamp;

                if ((currentTime - otpTime) <= 5 * 60 * 1000) {
                    // OTP verified and within the valid timeframe, save user details and login user
                    customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
                    customerService.save(customerDto);
                } else {
                    // OTP expired
                    model.addAttribute("error", "OTP has expired. Please request a new OTP.");
                    return "otp-verification";
                }
            } else {
                // Incorrect OTP
                model.addAttribute("error", "OTP is not valid");
                return "otp-verification";
            }
        } else {
            // CustomerDto or OTP not found in the session
            model.addAttribute("error", "Session expired. Please request a new OTP.");
            return "otp-verification";
        }

        // Clear session attributes
        session.removeAttribute("customerDto");
        session.removeAttribute("otp");
        session.removeAttribute("otpTimestamp");

        // Redirect to login page
        return "redirect:/login";
    }
// for resend otp 8  10
    @PostMapping("/do-register/resend-otp")
    @ResponseBody
    public ResponseEntity<String> resendOtp(HttpSession session) {
        try {
            String otp = smsService.generateOtp();
            smsService.sendOtp(otp);
            // Update the session with the new OTP and its generation timestamp
            session.setAttribute("otp", otp);
            session.setAttribute("otpTimestamp", System.currentTimeMillis());

            return ResponseEntity.ok("New OTP sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to resend OTP. Please try again later.");
        }
    }



//            if (customerDto.getPassword().equals(customerDto.getConfirmPassword())) {
//                customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
//                customerService.save(customerDto);
//                model.addAttribute("success", "Register successfully!");
//            } else {
//                model.addAttribute("error", "Password is incorrect");
//                model.addAttribute("customerDto", customerDto);
//                return "register";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            model.addAttribute("error", "Server is error, try again later!");
//        }
//        return "register";
}
