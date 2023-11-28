package com.example.library.service.Impl;

import com.example.library.dto.ProductDto;
import com.example.library.model.*;
import com.example.library.repository.CartItemRepository;
import com.example.library.repository.ShoppingCartRepository;
import com.example.library.service.CustomerService;
import com.example.library.service.ShoppingCartService;
import com.example.library.service.SizeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.example.library.model.CartItem;

import java.util.HashSet;
import java.util.Set;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
      private CustomerService customerService;
      private SizeService sizeService;
      private CartItemRepository cartItemRepository;
      private ShoppingCartRepository shoppingCartRepository;


    public ShoppingCartServiceImpl(CustomerService customerService,CartItemRepository cartItemRepository,ShoppingCartRepository shoppingCartRepository) {
        this.customerService = customerService;
       this.cartItemRepository =cartItemRepository;
       this.shoppingCartRepository=shoppingCartRepository;
    }

    @Override
    public ShoppingCart addItemToCart(ProductDto productDto,
                                      int quantity, String username)
    {

    Customer customer=customerService.findByUsername(username);
    ShoppingCart shoppingCart=customer.getCart();
    if(shoppingCart==null)
    {
        shoppingCart=new ShoppingCart();
    }
//        Size size= sizeService.findById(sizeId);
        Set<CartItem>cartItemList=shoppingCart.getCartItems();
        CartItem cartItem=find(cartItemList,productDto.getId());
        System.out.println(productDto.getId());
        Product product=transfer(productDto);
        System.out.println(product);

        double  unitPrice=0;

        if(productDto.getSalePrice()==0){
            unitPrice=productDto.getCostPrice();
        }else{
            unitPrice=productDto.getSalePrice();
        }

        int itemQuantity=0;
        if(cartItemList==null){
            cartItemList=new HashSet<>();

         if(productDto.getCurrentQuantity()<quantity){
             throw  new InsufficientQuantityException("Insufficient quantity available for product:"+product.getName());
         }
         if(cartItemList==null){
             cartItem=new CartItem();
             cartItem.setProduct(product);
             cartItem.setCart(shoppingCart);
             cartItem.setQuantity(quantity);
//             cartItem.setSize(size.getName());
             cartItem.setUnitPrice(unitPrice);
             cartItem.setCart(shoppingCart);
             cartItemList.add(cartItem);
             cartItemRepository.save(cartItem);
         }else{
             itemQuantity=cartItem.getQuantity()+quantity;
             cartItem.setQuantity(itemQuantity);
             cartItemRepository.save(cartItem);
         }
        } else{
            if(cartItem==null){
                cartItem=new CartItem();
                cartItem.setProduct(product);
                cartItem.setCart(shoppingCart);
                cartItem.setQuantity(quantity);
//                cartItem.setSize(size.getName());
                cartItem.setUnitPrice(unitPrice);
                cartItem.setCart(shoppingCart);
                cartItemList.add(cartItem);
                cartItemRepository.save(cartItem);
            }
//            else if(size.getName().equals(cartItem.getSize())){
//                itemQuantity=cartItem.getQuantity()+quantity;
//                cartItem.setQuantity(itemQuantity);
//                cartItemRepository.save(cartItem);
//            }
            else{
                cartItem=new CartItem();
                cartItem.setProduct(product);
                cartItem.setCart(shoppingCart);
                cartItem.setQuantity(quantity);
//                cartItem.setSize(size.getName());
                cartItem.setUnitPrice(unitPrice);
                cartItem.setCart(shoppingCart);
                cartItemList.add(cartItem);
                cartItemRepository.save(cartItem);
            }
        }
        shoppingCart.setCartItems(cartItemList);

        double totalPrice=totalPrice(shoppingCart.getCartItems());
        int totalItem=totalItem(shoppingCart.getCartItems());

        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setTotalItems(totalItem);
        shoppingCart.setCustomer(customer);


        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCart   updateCart(ProductDto productDto, int quantity, String username, Long cart_Item_id) {
        Customer customer=customerService.findByUsername(username);
        ShoppingCart shoppingCart=customer.getCart();
        Set<CartItem>cartItemList=shoppingCart.getCartItems();
        CartItem item=find(cartItemList, productDto.getId(), cart_Item_id);

        int itemQuantity=quantity;
//        if(size_id!=0){
//            Size size=sizeService.findById(size_id);
//            item.setSize(size.getName());
//        }
        item.setQuantity(itemQuantity);
        cartItemRepository.save(item);
        shoppingCart.setCartItems(cartItemList);
        int totalItem= totalItem(cartItemList);
        double totalprice=totalPrice(cartItemList);
        shoppingCart.setTotalPrice(totalprice);
        shoppingCart.setTotalItems(totalItem);
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCart removeItemFromCart(ProductDto productDto, String username) {
        Customer customer=customerService.findByUsername(username);
        ShoppingCart shoppingCart=customer.getCart();
        Set<CartItem>  cartItemList=shoppingCart.getCartItems();
        CartItem item=find(cartItemList,productDto.getId());
//        long id=item.getId();
//
//        cartItemRepository.deleteById(id);
//        return null;
        cartItemList.remove(item);
        cartItemRepository.deleteById(item.getId());
        double totalPrice = totalPrice(cartItemList);
        int totalItem = totalItem(cartItemList);
        shoppingCart.setCartItems(cartItemList);
        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setTotalItems(totalItem);
        if(cartItemList.isEmpty()){
            shoppingCart.setCustomer(null);
            shoppingCart.getCartItems().clear();
            shoppingCart.setTotalPrice(0);
            shoppingCart.setTotalItems(0);
        }
        return shoppingCartRepository.save(shoppingCart);

    }

    @Override
    @Transactional
    public void deleteCartById(Long id) {
        ShoppingCart shoppingCart=shoppingCartRepository.getReferenceById(id);
        for(CartItem cartItem:shoppingCart.getCartItems()){
            cartItem.setCart(null);
            cartItemRepository.deleteById(cartItem.getId());
        }
        shoppingCart.setCustomer(null);
        shoppingCart.getCartItems().clear();
        shoppingCart.setTotalPrice(0);
        shoppingCart.setTotalItems(0);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCart updateTotalPrice(Double newTotalPrice, String username) {
        Customer customer=customerService.findByUsername(username);
        ShoppingCart shoppingCart=customer.getCart();
        shoppingCart.setTotalPrice(newTotalPrice);
        shoppingCartRepository.save(shoppingCart);
        return shoppingCart;
    }

    private CartItem find(Set<CartItem>cartItems,long productId, String size){
        if(cartItems==null){
            return null;
        }
        CartItem cartItem=null;
        for(CartItem item:cartItems){
            if(item.getProduct().getId()==productId && size.equals(item.getSize())){
                cartItem=item;
            }
        }
        return cartItem;
    }

    private Product transfer(ProductDto productDto){
        Product  product=new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setCurrentQuantity(productDto.getCurrentQuantity());
        product.setCostPrice(productDto.getCostPrice());
        product.setSalePrice(product.getSalePrice());
//        product.setShortDescription(productDto.getShortDescription());
//        product.setLongDescription(productDto.getLongDescription());
            product.setImages(productDto.getImages());
//        product.setColors(productDto.getColors());
//        product.setBrand(productDto.getBrand());
            product.set_activated(productDto.isActivated());
            product.setCategory(productDto.getCategory());
            return product;

    }
    private double totalPrice(Set<CartItem>cartItemList){
        double totalprice=0.0;
        for(CartItem item:cartItemList){
            totalprice+=item.getUnitPrice()*item.getQuantity();
        }
        return totalprice;
    }

    private int totalItem(Set<CartItem>cartItemlist){
        int totalItem=0;
        for(CartItem item:cartItemlist){
            totalItem+=item.getQuantity();
        }return totalItem;
    }
    private CartItem find(Set<CartItem>cartItems,long productId,long cart_Item_Id){
        if(cartItems==null){
            return null;
        }
        CartItem cartItem=null;
        for(CartItem item:cartItems){
            if(item.getProduct().getId()==productId && item.getId()==cart_Item_Id){
                cartItem=item;

            }
        }return cartItem;
    }

    private CartItem find(Set<CartItem> cartItems, long productId) {
        if (cartItems == null) {
            return null;
        }
        CartItem cartItem = null;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                cartItem = item;
            }
        }
        return cartItem;
    }
}



















