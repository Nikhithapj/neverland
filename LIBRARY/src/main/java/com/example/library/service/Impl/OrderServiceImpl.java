package com.example.library.service.Impl;

import com.example.library.model.*;
import com.example.library.repository.OrderRepository;
import com.example.library.repository.ProductRepository;
import com.example.library.service.AddressService;
import com.example.library.repository.OrderDetailRepository;
import com.example.library.service.OrderService;
import com.example.library.service.ShoppingCartService;
import com.example.library.service.WalletService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderDetailRepository orderDetailRepository;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private AddressService addressService;
    private ShoppingCartService shoppingCartService;
    private WalletService walletService;

    public OrderServiceImpl(OrderDetailRepository orderDetailRepository, OrderRepository orderRepository, ProductRepository productRepository, AddressService addressService, ShoppingCartService shoppingCartService,WalletService walletService) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.addressService = addressService;
        this.shoppingCartService = shoppingCartService;
        this.walletService=walletService;
    }

    @Override
    public void updateOrderStatus(String status, long order_id) {
        if (order_id != 0) {
            Order order = orderRepository.getReferenceById(order_id);

            if (!order.getOrderStatus().equals("Returned") || !order.getOrderStatus().equals("Canceled")) {
                if (status.equals("Shipped")) {
                    order.setShippedDateTime(LocalDateTime.now());
                    order.setOrderStatus(status);
                    orderRepository.save(order);
                } else if (status.equals("Delivered")) {
                    order.setDeliveredDateTime(LocalDateTime.now());
                    order.setOrderStatus(status);
                    if (order.getPaymentMethod().equals("COD")) {
                        order.setPaymentStatus("paid");
                    }
                    orderRepository.save(order);
                }
            } else {
                System.out.println("order is already marked as returned");
            }
        }
    }

    @Override
    public List<Order> findAllOrders() {

        return orderRepository.findAll();
    }

    @Override
    public void acceptOrder(Long id) {
        Order order = orderRepository.getReferenceById(id);
        order.setAccept(true);
        Date oldDate = new Date();
        LocalDate localDate = oldDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate newLocalDate = localDate.plusWeeks(1);
        Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        order.setDeliveryDate(newDate);
        order.setConfirmedDateTime(LocalDateTime.now());
        order.setOrderStatus("confirmed");
        orderRepository.save(order);
    }

    @Override
    public void cancelOrder(long id) {
        Order order = orderRepository.getReferenceById(id);
        Customer customer = order.getCustomer();
        List<OrderDetail> orderDetailList = order.getOrderDetailsList();
        for (OrderDetail orderDetail : orderDetailList) {
            Product product = orderDetail.getProduct();
            if (product != null) {
                int currentQuantity = product.getCurrentQuantity();
                product.setCurrentQuantity(currentQuantity + orderDetail.getQuantity());
                productRepository.save(product);

            }
        }
        order.setOrderStatus("Cancelled");
        orderRepository.save(order);
        if(order.getPaymentMethod().equals("Wallet")||order.getPaymentMethod().equals("RazorPay"))
        {
            walletService.returnCredit(order,customer);
        }
    }

    @Override
    public Order findOrderById(long id) {
        return orderRepository.getReferenceById(id);
    }

    @Override
    public String getOrderStatus(Long id) {
        Order order = orderRepository.getReferenceById(id);
        String orderStatus = order.getOrderStatus();
        return orderStatus;
    }


    @Override
    public Order save(ShoppingCart cart, long address_id, String paymentMethod,Double oldTotalPrice) {
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setCustomer(cart.getCustomer());
        order.setTotalPrice(cart.getTotalPrice());
        order.setQuantity(cart.getTotalItems());
        order.setPaymentMethod(paymentMethod);
        order.setShippingAddress(addressService.findByIdOrder(address_id));
        order.setAccept(false);
        order.setOrderStatus("Pending");

       if(oldTotalPrice!=null){
           Double discount=oldTotalPrice-cart.getTotalPrice();
           String formattedDiscount=String.format("%.2f",discount);
           order.setDiscountprice(Double.parseDouble(formattedDiscount));
       }
       List <OrderDetail> orderDetailList=new ArrayList<>();
       List<String>outOfStockItems=new ArrayList<>();
       for(CartItem item: cart.getCartItems()){
           Product product=item.getProduct();
           int currentQuantity= product.getCurrentQuantity();
           if(currentQuantity>=item.getQuantity()){
               product.setCurrentQuantity(currentQuantity-item.getQuantity());
               productRepository.save(product);
               OrderDetail orderDetail=new OrderDetail();
               orderDetail.setOrder(order);
               orderDetail.setProduct(item.getProduct());
               orderDetail.setQuantity(item.getQuantity());
               orderDetailRepository.save(orderDetail);

           }else{
               outOfStockItems.add(item.getProduct().getName());
           }
       }
       if(!outOfStockItems.isEmpty()){
           StringBuilder errorMessage=new StringBuilder("The following items " +
                   "are not available in sufficient quantity:");
           errorMessage.append(String.join (",",outOfStockItems));
           throw new OutOfStockException(errorMessage.toString());
       }

       order.setOrderDetailsList(orderDetailList);
       if(paymentMethod.equals("COD")){
           order.setPaymentStatus("pending");
           shoppingCartService.deleteCartById(cart.getId());
       }else if(paymentMethod.equals("Wallet")){
           order.setPaymentStatus("paid");
           shoppingCartService.deleteCartById(cart.getId());
       }
//        if(productDto.getCurrentQuantity()<quantity){
//            throw  new InsufficientQuantityException("Insufficient quantity available for product:"+product.getName());
//        }
       return  orderRepository.save(order);
    }

    @Override
    public void updatePayment(Order order, boolean status)
    {
          if(status){
              order.setPaymentStatus("paid");
              orderRepository.save(order);
          }else{
              order.setPaymentStatus("Failed");
              orderRepository.save(order);
          }
    }

}

























