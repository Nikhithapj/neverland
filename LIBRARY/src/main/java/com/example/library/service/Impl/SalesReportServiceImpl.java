package com.example.library.service.Impl;

import com.example.library.model.Product;
import com.example.library.repository.OrderDetailRepository;
import com.example.library.repository.ProductRepository;
import com.example.library.service.SalesReportService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class SalesReportServiceImpl implements SalesReportService
{
    private ProductRepository productRepository;
    private OrderDetailRepository orderDetailRepository;

    public SalesReportServiceImpl(ProductRepository productRepository, OrderDetailRepository orderDetailRepository)
    {
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public List<Product> findAllProducts()
    {
        return productRepository.findAll();
    }

    @Override
    public List<Object[]> findProductSoldAndEarning() {
        return orderDetailRepository.findProductsSoldAndRevenue();
    }

    @Override
    public List<Object[]> findProductsSoldAndEarningsFilter(int month, int year) {
        return orderDetailRepository.findProductsSoldAndRevenueByMonthAndYear(month, year);
    }
}
