package com.example.library.service.Impl;

import com.example.library.exceptions.DuplicateWishlistNameException;
import com.example.library.model.Customer;
import com.example.library.model.Product;
import com.example.library.model.Wishlist;
import com.example.library.repository.WishlistRepository;
import com.example.library.service.ProductService;
import com.example.library.service.WishlistService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class WishlistServiceImpl implements WishlistService {

    public WishlistServiceImpl(WishlistRepository wishlistRepository,ProductService productService) {
        this.wishlistRepository= wishlistRepository;
        this.productService= productService;
    }
    private ProductService productService;
    private WishlistRepository wishlistRepository;
    @Override
    public List<Wishlist> findAllByCustomer(Customer customer) {
        List<Wishlist>Wishlists=wishlistRepository.findAllByCustomerId(customer.getId());
        return Wishlists;
    }

    @Override
    public void createWishlist(String wishlistName, Customer customer) {
        Wishlist existingWishlist=wishlistRepository.findByCustomerAndWishlistName(customer,wishlistName);
        if(existingWishlist!=null){
            throw new DuplicateWishlistNameException("a wishlist with same name already exists");
        }


        Wishlist wishlist=new Wishlist();
        wishlist.setWishlistName(wishlistName);
        wishlist.setCustomer(customer);
        wishlistRepository.save(wishlist);
    }

    @Override
    public Wishlist findById(long id) {
        return wishlistRepository.findById(id);
    }

//    @Override
//    public boolean findByProductId(long productId, Customer customer) {
//        boolean exists=wishlistRepository.findByProductIdAndCustomerId(productId, customer.getId());
//        System.out.println(exists);
//        return exists;
//    }

    @Override
    public boolean findByProductId(long productId, Customer customer) {
        Long count = wishlistRepository.findByProductIdAndCustomerId(productId, customer.getId());
        System.out.println(count);
        return count != null && count > 0;
    }


    @Override
    public Wishlist save(Long productId, Customer customer) {
        Product product=productService.findById(productId);
        Wishlist wishlist=new Wishlist();
        wishlist.setProduct(product);
        wishlist.setCustomer(customer);
        wishlistRepository.save(wishlist);
        return wishlist;
    }

    @Override
    public void deleteWishlist(long id) {
        Wishlist wishlist=wishlistRepository.findById(id);
        wishlistRepository.delete(wishlist);
    }


}
