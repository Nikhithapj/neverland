package com.example.library.service;

import com.example.library.model.Order;
import com.example.library.model.ShoppingCart;

import java.util.List;

public interface OrderService {

    void updateOrderStatus(String status ,long order_id);

    List<Order> findAllOrders();

    void acceptOrder(Long id);

    void cancelOrder(long id);

    Order findOrderById(long id);

    String getOrderStatus(Long orderId);

    Order save(ShoppingCart cart, long address_Id, String paymentMethod,Double oldTotalPrice);

    void updatePayment(Order order,boolean status);


    /* for dashboard */

//    Double getTotalOrderAmount();

}
