package com.example.library.service.Impl;

import com.example.library.dto.CustomerDto;
import com.example.library.model.Address;
import com.example.library.model.Customer;
import com.example.library.model.EmailDetails;
import com.example.library.model.PasswordDTO;
import com.example.library.repository.CustomerRepository;
import com.example.library.repository.RoleRepository;
import com.example.library.service.CustomerService;
import com.example.library.service.EmailService;
import com.example.library.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
   private EmailService emailService;
   private WalletService walletService;

    public CustomerServiceImpl(CustomerRepository customerRepository, RoleRepository roleRepository, EmailService emailService, WalletService walletService) {
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.walletService = walletService;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPassword(customerDto.getPassword());
        customer.setUsername(customerDto.getUsername());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        String enteredRefferal=customerDto.getReferralCode();
        if(enteredRefferal!=null){
            try{
                Customer referralOwnerCustomer=customerRepository.findByReferralCode(enteredRefferal);
                if(referralOwnerCustomer!=null){
                    boolean status=walletService.saveReferralOffer(200.00,referralOwnerCustomer);
                    if(!status){
                        throw  new RuntimeException("Referral offer transaction failed");
                    }
                }
            }catch (NullPointerException e){
                throw new RuntimeException("no refree found");
            }
        }
        customer.setReferralCode(referralCodeGenerator());
        Customer customerSave=customerRepository.save(customer);
        return mapperDto(customerSave);
    }


    private String referralCodeGenerator(){
        char[]chars="ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb=new StringBuilder();
        Random random=new SecureRandom();
        for(int i=0;i<8;i++){
            char c =chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output=sb.toString();
        return  output;
    }

    private  CustomerDto mapperDto(Customer customer){
        CustomerDto customerDto=new CustomerDto();
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setUsername(customer.getUsername());
        customerDto.setPassword(customer.getPassword());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
         return customerDto;
    }

    @Override
    public Customer findByUsername(String username) {

        return customerRepository.findByUsername(username);
    }


    @Override
    public CustomerDto getCustomer(String username) {
        CustomerDto customerDto = new CustomerDto();
        Customer customer = customerRepository.findByUsername(username);
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setUsername(customer.getUsername());
        customerDto.setPassword(customer.getPassword());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
//        customerDto.setCity(customer.getCity());
//        customerDto.setCountry(customer.getCountry());
        return customerDto;
    }

    @Override
    public CustomerDto findByEmailCustomerDto(String username) {
        Customer customer=customerRepository.findByUsername(username);
        CustomerDto customerDto=new CustomerDto();
        customerDto.setUsername(customer.getUsername());
        customerDto.setId(customer.getId());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.setAddress(customerDto.getAddress());
        customerDto.setPassword(customerDto.getPassword());
        customerDto.set_activated(customerDto.is_activated());
        return customerDto;
    }

    @Override
    public CustomerDto updateAccount(CustomerDto customerDto, String username)
    {
    Customer customer=findByUsername(username);
    customer.setFirstName(customerDto.getFirstName());
    customer.setLastName(customer.getLastName());
    customer.setPhoneNumber(customer.getPhoneNumber());
    customerRepository.save(customer);
    CustomerDto  customerDtoUpdated=convertEntityDto(customer);
    return customerDtoUpdated;
    }

    @Override
    public Customer saveInfo(Customer customer, Address address) {
    Customer customer1=customerRepository.findByUsername(customer.getUsername());
    List<Address>addressList=customer1.getAddress();
    if(addressList==null){
        addressList=new ArrayList<>();
    }
     addressList .add(address);
    customer1.setAddress(addressList);
    return customerRepository.save(customer1);
    }

    @Override
    public Customer findById(long id) {
        return customerRepository.findById(id);
    }

    @Override
    public void updateUserProfile(CustomerDto customerDto) {
        Customer customer = customerRepository.findByUsername(customerDto.getUsername());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setUpdateOn(new Date());

        customerRepository.save(customer);
    }

    @Override
    public Customer updates(CustomerDto customerDto, long id) {
        Customer customer=customerRepository.findById(id);

        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhoneNumber(customerDto.getPhoneNumber());

        return customerRepository.save(customer);
    }

    @Override
    public void changePassword(long id,PasswordDTO passwordDTO) {
        Customer customer=  customerRepository.findById(id);
        customer.setPassword(passwordDTO.getPassword());
        customer.setUpdateOn(new Date());
        customerRepository.save(customer);

    }


    private CustomerDto convertEntityDto(Customer customer) {
        CustomerDto customerDto=new CustomerDto();
        customerDto.setId(customer.getId());
        customerDto.setFirstName(customerDto.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.set_activated(customer.is_activated());
        customerDto.setPassword(customer.getPassword());
        return customerDto;
    }

    @Override
    public Customer changePass(CustomerDto customerDto) {
        Customer customer = customerRepository.findByUsername(customerDto.getUsername());
        customer.setPassword(customerDto.getPassword());
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(CustomerDto dto) {
        Customer customer = customerRepository.findByUsername(dto.getUsername());
//        customer.setAddress(dto.getAddress());
//        customer.setCity(dto.getCity());
//        customer.setCountry(dto.getCountry());
        customer.setPhoneNumber(dto.getPhoneNumber());
        return customerRepository.save(customer);
    }

        @Override
        public CustomerDto findByIdProfile(Long id) {
            Customer customer=customerRepository.getReferenceById(id);
            CustomerDto customerDto =new CustomerDto();
            customerDto.setId(customer.getId());
            customerDto.setLastName(customer.getLastName());
            customerDto.setFirstName(customer.getFirstName());
            customerDto.setPhoneNumber(customer.getPhoneNumber());
            return customerDto;
    }

    @Override
    public String shareRefferalCode(String refferalCode, String emailAddress) {
        return emailService.sendSimpleEmail(new EmailDetails(emailAddress,"Hey  I wanted to share my referral code for nevelands with you: "+refferalCode,
                "Check out my neverlands referral code!"));
    }

    @Override
    public Customer findByReferralCode(String referralCode) {
        return customerRepository.findByReferralCode(referralCode);
    }

    @Override
    public List<CustomerDto> findAll()  {
        List<CustomerDto>  customerDtoList = new ArrayList<>();
        List<Customer> customers = (List<Customer>) customerRepository.findAll();
        for(Customer customer:customers){
            CustomerDto customerDto = new CustomerDto();
            customerDto.setId(customer.getId());
            customerDto.setFirstName(customer.getFirstName());
            customerDto.setLastName(customer.getLastName());
            customerDto.setUsername(customer.getUsername());
            customerDto.setPhoneNumber(customer.getPhoneNumber());
            customerDto.setBlocked(customer.is_blocked());
            customerDtoList.add(customerDto);
        }
        return customerDtoList;
    }

//    @Override
//    public void blockUser(Long id) {
//        Customer customer = customerRepository.getReferenceById(id);
//        if (customer.is_blocked()){
//            customer.set_blocked(false);
//        }
//        else {
//            customer.set_blocked(true);
//        }
//        customerRepository.save(customer);
//
//
//    }

  public void blockById(Long id){
        Customer customer=customerRepository.getReferenceById(id);
        customer.set_blocked(true);
        customerRepository.save(customer);
  }

    public void unblockById(Long id){
        Customer customer=customerRepository.getReferenceById(id);
        customer.set_blocked(false);
        customerRepository.save(customer);
    }




}
