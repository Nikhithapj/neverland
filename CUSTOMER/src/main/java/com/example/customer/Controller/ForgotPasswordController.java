package com.example.customer.Controller;

import com.example.library.model.Customer;
import com.example.library.service.Impl.CustomerForgotPassword;
import com.example.library.service.Impl.CustomerNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {

    private JavaMailSender mailSender;
    private CustomerForgotPassword customerForgotPassword;

    public ForgotPasswordController(JavaMailSender mailSender, CustomerForgotPassword customerForgotPassword) {
        this.mailSender = mailSender;
        this.customerForgotPassword = customerForgotPassword;
    }

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processForgotPasswordForm(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomStringUtils.randomAlphanumeric(30);
        try {
            customerForgotPassword.updateResetpasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            System.out.println(resetPasswordLink);
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
        }
        catch (CustomerNotFoundException ex)

        {
            model.addAttribute("error", ex.getMessage());
        }
        catch (UnsupportedEncodingException | MessagingException e)
        {
            model.addAttribute("error", "Error while sending email");
        }
        return "forgot_password_form";
    }

    public void sendEmail(String recepientEmail, String link) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("nikhithapj6@gmail.com", "Nikhitha Joy");
        helper.setTo(recepientEmail);

        String subject = "here s the link to reset your password";
        String content = "<p>Hello,<p/>"
                + "<p>you have requested to reset your password</p>"
                + "<p>click the link below to change your password</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password,"
                + "or you have not made this request </p>";

        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);

    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        Customer customer = customerForgotPassword.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (customer == null) {
            model.addAttribute("message", "Invalid  token");
            return "message";
        }
        return "reset_password_form";

    }

    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request,Model model) {
        String token=request.getParameter("token");
        String password=request.getParameter("password");

        Customer customer=customerForgotPassword.getByResetPasswordToken(token);

        if(customer==null){
                   model.addAttribute("title","reset your password");

            model.addAttribute("message","invalid token");

        }
        else {
            customerForgotPassword.updatePassword(customer,password);
            model.addAttribute("message","you have succesfully changed your password");
        }

        return "succ";

    }
}






























