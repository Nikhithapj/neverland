package com.example.library.service;

import com.example.library.model.Product;

import java.util.List;

public interface SalesReportService {
List<Product> findAllProducts();
List<Object[]>findProductSoldAndEarning();
List<Object[]>findProductsSoldAndEarningsFilter(int month,int year);
}
