package com.example.library.service;



import com.example.library.dto.CustomerDto;
import com.example.library.model.Address;
import com.example.library.model.Customer;
import com.example.library.model.PasswordDTO;

import java.util.List;

public interface CustomerService {

    CustomerDto save(CustomerDto customerDto);

    Customer findByUsername(String username);

    Customer update(CustomerDto customerDto);

    CustomerDto findByIdProfile(Long id);

    String shareRefferalCode(String refferalCode,String emailAddress);


    Customer findByReferralCode(String referralCode);
    List<CustomerDto> findAll();

//    void blockUser(Long id);

       void blockById(Long id);

     void unblockById(Long id);

    Customer changePass(CustomerDto customerDto);

    CustomerDto getCustomer(String username);

    CustomerDto findByEmailCustomerDto(String username);

    CustomerDto updateAccount(CustomerDto customerDto,String username);

    Customer saveInfo(Customer customer, Address address);

    Customer findById(long id);
     void updateUserProfile(CustomerDto customerDto);

    Customer updates(CustomerDto customerDto, long id);


    void changePassword(long id, PasswordDTO passwordDTO);
}
























