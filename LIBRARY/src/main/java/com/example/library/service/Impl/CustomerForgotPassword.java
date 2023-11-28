package com.example.library.service.Impl;

import com.example.library.model.Customer;
import com.example.library.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CustomerForgotPassword {

    private CustomerRepository customerRepository;

    public CustomerForgotPassword(CustomerRepository customerRepository)
    {

        this.customerRepository=customerRepository;

    }

    public void updateResetpasswordToken(String token,String username)throws CustomerNotFoundException {

        Customer customer=customerRepository.findByUsername(username);
        if(customer!=null){
            customer.setResetPasswordToken(token);
            customerRepository.save(customer);
        }
        else{
            throw new CustomerNotFoundException("could not find any customer with the email"+username);
        }

}

public Customer getByResetPasswordToken(String token){

        return customerRepository.findByResetPasswordToken(token);
}
public void updatePassword(Customer customer,String newPassword){
    BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    String encodedPassword=passwordEncoder.encode(newPassword);
    customer.setPassword(encodedPassword);

    customer.setResetPasswordToken(null);
    customerRepository.save(customer);
}

}
