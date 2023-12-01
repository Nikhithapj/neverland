package com.example.library.service.Impl;

import com.example.library.dto.ProductDto;
import com.example.library.model.Image;
import com.example.library.model.Product;
import com.example.library.repository.ImageRepository;
import com.example.library.repository.ProductRepository;
import com.example.library.service.ProductService;
import com.example.library.service.utils.ImageUpload;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService
{
    private final ProductRepository productRepository;
    private final ImageUpload imageUpload;
    private final ImageRepository imageRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<ProductDto> products() {
        return transferData(productRepository.getAllProduct());
    }

    @Override
    public List<ProductDto> allProduct() {
        List<Product> products = productRepository.findAll();
        List<ProductDto> productDtos = transferData(products);
        return productDtos;
    }

    @Override
    public ProductDto findById(long id) {
        Product product=productRepository.findById(id);
        ProductDto productDto=new ProductDto();
        productDto.setId(product.getId());
        productDto.setImages(product.getImages());
        productDto.setName(product.getName());
//        productDto.setShortDescription(product.getShortDescription());
//        productDto.setLongDescription(product.getLongDescription());
//        productDto.setBrand(product.getBrand());
//        productDto.setColors(product.getColors());
        productDto.setSizes(product.getSizes());
        productDto.setCurrentQuantity(product.getCurrentQuantity());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setCategory(product.getCategory());
        productDto.setActivated(product.is_activated());
        return productDto;
    }

    @Override
    public List<Product> findAllByCategoryAnd_activated(String category) {
        return null;
    }
//    @Override
//    public List<Product> findAllByCategoryAnd_activated(String category){
//        return  productRepository.findAllByCategoryAnd_activated(findAllByCategory(category));
//    }
//
//
//    @Override
//    public Product save(List<MultipartFile> imageProducts, ProductDto productDto) {
//        Product product = new Product();
//        product.setName(productDto.getName());
//        product.setDescription(productDto.getDescription());
//        product.setCurrentQuantity(productDto.getCurrentQuantity());
//        product.setCostPrice(productDto.getCostPrice());
//        product.setCategory(productDto.getCategory());
//        product.set_deleted(false);
//        product.set_activated(true);
//        productRepository.save(product);
//        try {
//            List<Image> images = new ArrayList<>();
//            for (MultipartFile imageProduct : imageProducts) {
//                Image image = new Image();
//                imageUpload.uploadFile(imageProduct);
//                image.setName(Base64.getEncoder().encodeToString(imageProduct.getBytes()));
//                image.setProduct(product);
//                images.add(image);
//                imageRepository.save(image);
//            }
//            product.setImages(images);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return product;
//    }
//commented the code under to add resizing image

     @Transactional
     @Override
        public Product save(List<MultipartFile> imageProducts, ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCurrentQuantity(productDto.getCurrentQuantity());

        product.setCostPrice(productDto.getCostPrice());
        product.setCategory(productDto.getCategory());
        product.set_deleted(false);
        product.set_activated(true);
        productRepository.save(product);
        try {
            List<Image> images = new ArrayList<>();
            for (MultipartFile imageProduct : imageProducts) {
                Image image = new Image();
                imageUpload.uploadFile(imageProduct);
                image.setName(Base64.getEncoder().encodeToString(imageProduct.getBytes()));
                image.setProduct(product);
                images.add(image);
                imageRepository.save(image);
            }
            product.setImages(images);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return product;
    }

    /* Updating the image*/
    @Override
    public Product update(List<MultipartFile> ImageProducts, ProductDto productDto) {
        try {
            System.out.println(ImageProducts);
            long id = productDto.getId();
            // Retrieve the existing product from the repository
            Product productUpdate = productRepository.findById(id);

            // Update product properties from the DTO
            productUpdate.setCategory(productDto.getCategory());
            productUpdate.setName(productDto.getName());
            productUpdate.setDescription(productDto.getDescription());
            productUpdate.setCostPrice(productDto.getCostPrice());
            productUpdate.setSalePrice(productDto.getSalePrice());
            productUpdate.setCurrentQuantity(productDto.getCurrentQuantity());
//            productRepository.save(productUpdate);

            // Handle image update
            if (!ImageProducts.isEmpty() && ImageProducts != null) {

                List<Image> imageList = new ArrayList<>();
                List<Image> image = imageRepository.findImageBy(id);
                int i=0;
                for (MultipartFile imageProduct : ImageProducts) {
                    imageUpload.uploadFile(imageProduct);
                    image.get(i).setName(Base64.getEncoder().encodeToString(imageProduct.getBytes()));
                    imageRepository.save(image.get(i));
                    imageList.add(image.get(i));
                    i++;
                }
                productUpdate.setImages(imageList);
            }// Add debug statements


            return productRepository.save(productUpdate);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    @Override
    public void enableById(Long id) {
        Product product = productRepository.getById(id);
        product.set_activated(true);
        product.set_deleted(false);
        productRepository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        Product product = productRepository.getById(id);
        product.set_deleted(true);
        product.set_activated(false);
        productRepository.save(product);
    }

//    @Override
//    public void hardDeleteById(Long id) {
//        productRepository.deleteById(id);
//        // Physically delete the product from the database
//    }

    @Override
    public ProductDto getById(Long id){
        ProductDto productDto=new ProductDto();
        Product product=productRepository.getReferenceById(id);
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setCurrentQuantity(product.getCurrentQuantity());
        productDto.setCategory(product.getCategory());
        productDto.setImages(product.getImages());
        return productDto;

    }


    @Override
    public Product findById(Long id)
    {
        return productRepository.findById(id).get();
    }

    @Override
    public List<ProductDto> findAllProducts() {
        List<Product>products=productRepository.findAllByActivatedTrue();
        List<ProductDto>productDtoList=transferData(products);
        return productDtoList;
    }

    @Override
    public List<ProductDto> randomProduct() {
        return transferData(productRepository.randomProduct());
    }

    @Override
    public Page<ProductDto> searchProducts(int pageNo, String keyword) {
        List<Product> products = productRepository.findAllByNameOrDescription(keyword);
        List<ProductDto> productDtoList = transferData(products);
        Pageable pageable = PageRequest.of(pageNo, 5);
        Page<ProductDto> dtoPage = toPage(productDtoList, pageable);
        return dtoPage;
    }

    @Override
    public Page<ProductDto> getAllProducts(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 6);
        List<ProductDto> productDtoLists = this.allProduct();
        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
        return productDtoPage;
    }

    @Override
    public Page<ProductDto> getAllProductsForCustomer(int pageNo) {
        return null;
    }

    @Override
    public List<ProductDto> findAllByCategory(String category) {
        return transferData(productRepository.findAllByCategory(category));
    }

    @Override
    public List<Product> findProductsByCategory(long id) {
        return productRepository.findAllByCategoryId(id);
    }

    @Override
    public List<ProductDto> filterHighProducts() {
        return transferData(productRepository.filterHighProducts());
    }

    @Override
    public List<ProductDto> filterLowerProducts() {
        return transferData(productRepository.filterLowerProducts());
    }

    @Override
    public List<ProductDto> listViewProducts() {
        return transferData(productRepository.listViewProduct());
    }

    @Override
    public List<ProductDto> findByCategoryId(Long id) {
        return transferData(productRepository.getProductByCategoryId(id));
    }

    @Override
    public List<ProductDto> searchProducts(String keyword) {
        return transferData(productRepository.searchProducts(keyword));
    }

    private Page toPage(List list, Pageable pageable) {
        if (pageable.getOffset() >= list.size()) {
            return Page.empty();
        }
        int startIndex = (int) pageable.getOffset();
        int endIndex = ((pageable.getOffset() + pageable.getPageSize()) > list.size())
                ? list.size()
                : (int) (pageable.getOffset() + pageable.getPageSize());
        List subList = list.subList(startIndex, endIndex);
        return new PageImpl(subList, pageable, list.size());
    }

    private List<ProductDto> transferData(List<Product> products) {
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setCurrentQuantity(product.getCurrentQuantity());
            productDto.setCostPrice(product.getCostPrice());
            productDto.setSalePrice(product.getSalePrice());
            productDto.setDescription(product.getDescription());
            productDto.setImages(product.getImages());
            productDto.setCategory(product.getCategory());
            productDto.setActivated(product.is_activated());
            productDto.setDeleted(product.is_deleted());
            productDtos.add(productDto);
        }
        return productDtos;
    }
    @Override
    public List<ProductDto> findByCategory(Long id) {
        List<Product> productList = productRepository.findAllByCategoryId(id);
        return transferData(productRepository.findAllByCategoryId(id));

}

    @Override
    public List<Object[]> getTotalQuantityPerProduct() {
        return  productRepository.getTotalQuantityPerProduct();
    }

    @Override
    public List<Object[]> getProductsStatsBetweenDates(Date startDate, Date endDate) {
        return productRepository.getProductsStatsForConfirmedOrdersBetweenDates(startDate,endDate);

    }

    @Override
    public List<Object[]> getProductStats() {
        return productRepository.getProductStatsForConfirmedOrders();

    }
}