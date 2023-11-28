package com.example.library.service.Impl;

import com.example.library.model.Order;
import com.example.library.repository.OrderRepository;
import com.example.library.service.DashBoardService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DashBoardServiceImpl implements DashBoardService {
    private final OrderRepository orderRepository;

    public DashBoardServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public double findCurrentMonthOrder(Date startDate, Date endDate) {
        List<Order>orderList=orderRepository.findByOrderDateBetween(startDate,endDate);
        double ordersTotalPrice=0;
        if(!orderList.isEmpty()){
            for(Order orders:orderList) {
                ordersTotalPrice = ordersTotalPrice + orders.getTotalPrice();
            }
        }
        System.out.println(ordersTotalPrice);
        System.out.println("hi");
        return ordersTotalPrice;
    }

    @Override
    public long findOrdersTotal() {
        return orderRepository.count();
    }

    @Override
    public int findOrdersPending() {
        return orderRepository.countByIsAcceptIsFalse();
    }

    @Override
    public List<Object[]> retrieveDailyEarnings(int currentYr, int currentMnt)
    {
        return orderRepository.dailyEarnings(currentYr,currentMnt);
    }

    @Override
    public List<Object[]> findTotalPricesByPayment() {
        return orderRepository.findTotalPricesByPaymentMethod();
    }
}
