package com.example.library.repository;

import com.example.library.model.Customer;
import com.example.library.model.Wishlist;
import net.minidev.json.JSONUtil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository  extends JpaRepository<Wishlist,Long> {
    List<Wishlist> findAllByCustomerId(long id);

    Wishlist findByCustomerAndWishlistName(Customer customer,String wishlistName);

    Wishlist findById(long id);

//    @Query(value = "SELECT EXISTS(SELECT FROM wishlist WHERE customer_id=:customerId AND product_id=:productId)",nativeQuery = true)
//    boolean findByProductIdAndCustomerId(@Param("productId")long productId,@Param("customerId")long customerId);
//        @Query(value = "SELECT COUNT(*) > 0 FROM wishlist WHERE customer_id = :customerId AND product_id = :productId", nativeQuery = true)
//        boolean findByProductIdAndCustomerId(@Param("productId") long productId, @Param("customerId") long customerId);

    @Query(value = "SELECT COUNT(*) FROM wishlist WHERE customer_id = :customerId AND product_id = :productId", nativeQuery = true)
    Long findByProductIdAndCustomerId(@Param("productId") long productId, @Param("customerId") long customerId);




}
