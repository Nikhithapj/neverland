package com.example.library.service;

import com.example.library.dto.ProductDto;
import com.example.library.model.ShoppingCart;

public interface ShoppingCartService {

    ShoppingCart addItemToCart(ProductDto productDto,int quantity,String username);

    ShoppingCart updateCart(ProductDto productDto,int quantity,String username,Long cart_Item_id);

    ShoppingCart removeItemFromCart(ProductDto productDto,String username);

    void deleteCartById(Long id);

    ShoppingCart updateTotalPrice(Double newTotalPrice,String username);

}
