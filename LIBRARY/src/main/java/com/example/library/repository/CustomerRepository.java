package com.example.library.repository;

import com.example.library.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>
    //before it was jparepository that extends
{
//    Customer findByUsername(String username);
    @Query("select c from Customer c where c.username = ?1")
     Customer findByUsername(String username);

    public Customer findByResetPasswordToken(String token);

    Customer findById(long id);

    Customer findByReferralCode(String refferalCode);
}
