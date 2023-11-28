package com.example.library.service;

import com.example.library.dto.ProductDto;
import com.example.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<Product> findAll();

    List<ProductDto> products();

    List<ProductDto> allProduct();

    ProductDto findById(long id);
    List<Product> findAllByCategoryAnd_activated(String category);

    Product save(List<MultipartFile> imageProduct, ProductDto product);

    Product update(List<MultipartFile> imageProduct, ProductDto productDto);

    void enableById(Long id);

    void deleteById(Long id);
//    void hardDeleteById(Long id);

    ProductDto getById(Long id);

    Product findById(Long id);

    List<ProductDto>findAllProducts();

    List<ProductDto> randomProduct();

    Page<ProductDto> searchProducts(int pageNo, String keyword);

    Page<ProductDto> getAllProducts(int pageNo);

    Page<ProductDto> getAllProductsForCustomer(int pageNo);


    List<ProductDto> findAllByCategory(String category);

List<Product>findProductsByCategory(long id);

    List<ProductDto> filterHighProducts();

    List<ProductDto> filterLowerProducts();

    List<ProductDto> listViewProducts();

    List<ProductDto> findByCategoryId(Long id);

    List<ProductDto> searchProducts(String keyword);


    List<ProductDto> findByCategory(Long id);

    List<Object[]>getTotalQuantityPerProduct();
}