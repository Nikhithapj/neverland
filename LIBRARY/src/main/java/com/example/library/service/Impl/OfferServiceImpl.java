package com.example.library.service.Impl;

import com.example.library.model.Category;
import com.example.library.model.Product;
import com.example.library.repository.ProductRepository;
import com.example.library.service.CategoryService;
import com.example.library.service.OfferService;
import com.example.library.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;


import com.example.library.dto.OfferDto;
import com.example.library.model.Offer;
import com.example.library.repository.OfferRepository;
import com.example.library.service.OfferService;

import java.util.ArrayList;
import java.util.List;
@Service
public class OfferServiceImpl implements OfferService {
    private OfferRepository offerRepository;
    private ProductService productService;
    private ProductRepository productRepository;
    private CategoryService categoryService;
    public OfferServiceImpl(OfferRepository offerRepository, ProductService productService, ProductRepository productRepository, CategoryService categoryService) {
        this.offerRepository = offerRepository;
        this.productService = productService;
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Override
    public List<OfferDto> getAllOffers() {
        List<Offer>offerList=offerRepository.findAll();
        List<OfferDto>offerDtoList=transfer(offerList);
        return offerDtoList;
    }

    @Override
    public Offer save(OfferDto offerDto) {
        Offer offer=new Offer();
        offer.setName(offerDto.getName());
        offer.setDescription(offerDto.getDescription());
        offer.setOffPercentage(offerDto.getOffPercentage());
        offer.setOfferType(offerDto.getOfferType());
        System.out.println("1"+offer.getOfferType());
        offer.setEnabled(true);
        if(offerDto.getOfferType().equals("Product")){
            Product product=productService.findById(offerDto.getOfferProductId());
            offer.setOfferProductId(offerDto.getOfferProductId());
            Double oldDiscount=(Double)product.getCostPrice()*((double)offerDto.getOffPercentage()/100.0);
            String formattedDiscount=String.format("%.2f",oldDiscount);
            Double discount=Double.parseDouble(formattedDiscount);
            String formattedSalesPrice=String.format("%.2f",product.getCostPrice()-discount);
            Double salePrice=Double.parseDouble(formattedSalesPrice);
            product.setSalePrice(salePrice);
            offer.setApplicableForProductName(product.getName());
            productRepository.save(product);
        }else{
            long applicable_id=offerDto.getOfferCategoryId();
            Category category=categoryService.findById(applicable_id);
            offer.setOfferCategoryId(offerDto.getOfferCategoryId());
            offer.setApplicableForCategoryName(category.getName());
            List<Product>productList=productService.findProductsByCategory(category.getId());
            for(Product product:productList){
                Double oldDiscount=(double)product.getCostPrice()*((double)offerDto.getOffPercentage()/100.0);
                String formattedDiscount=String.format("%.2f",oldDiscount);
                Double discount=Double.parseDouble(formattedDiscount);
                String formattedSalePrice=String.format("%.2f",product.getCostPrice()-discount);
                Double salePrice=Double.parseDouble(formattedSalePrice);
                product.setSalePrice(salePrice);
                productRepository.save(product);
            }
 }

        offerRepository.save(offer);
        return offer;
    }

    @Override
    public Offer update(OfferDto offerDto) {
        long id=offerDto.getId();
        Offer offer=offerRepository.findById(id);
        offer.setName(offerDto.getName());
        offer.setDescription(offerDto.getDescription());
        offer.setOffPercentage(offerDto.getOffPercentage());
        offer.setOfferType(offer.getOfferType());
        if(offerDto.getOfferType().equals("Product")){
            if(offer.getOfferProductId()!=null){
                if(offerDto.getOfferProductId()!=offer.getOfferProductId()){
                    updateProductPrice(offer.getOfferProductId());
                }}
                else{
                    updateProductPrice(offer.getOfferCategoryId());
                    offer.setOfferCategoryId(null);

                }
                Product product=productService.findById(offerDto.getOfferProductId());
                offer.setOfferProductId(offerDto.getOfferProductId());
                Double oldDiscount=(Double) product.getCostPrice()*((double)offerDto.getOffPercentage()/100.0);
                String formattedDiscount=String.format("%.2f",oldDiscount);
                Double discount=Double.parseDouble(formattedDiscount);
                String formattedSalePrice=String.format("%.2f",product.getCostPrice()-discount);
                Double salePrice=Double.parseDouble(formattedSalePrice);
                product.setSalePrice(salePrice);
                offer.setApplicableForProductName(product.getName());
                productRepository.save(product);
            }
            else{
                if(offer.getOfferCategoryId()!=null){
                    if(offerDto.getOfferCategoryId()!=offer.getOfferCategoryId()){
                        updateCategoryPrice(offer.getOfferCategoryId());
                    }
                }
                else{
                    updateProductPrice(offer.getOfferCategoryId());
                    offer.setOfferProductId(null);
                }
                long applicable_id=offerDto.getOfferCategoryId();
                Category category=categoryService.findById(applicable_id);
                offer.setOfferCategoryId(offerDto.getOfferCategoryId());
                offer.setApplicableForCategoryName(category.getName());
                List<Product>productList=productService.findProductsByCategory(category.getId());
                for(Product product:productList){
                    Double oldDiscount=(Double)product.getCostPrice()*((double)offerDto.getOffPercentage()/100.0);
                    String formattedDiscount=String.format("%.2f",oldDiscount);
                    Double discount=Double.parseDouble(formattedDiscount);
                    String formattedSalePrice=String.format("%.2f",product.getCostPrice()-discount);
                    Double salePrice=Double.parseDouble(formattedSalePrice);
                    product.setSalePrice(salePrice);
                    productRepository.save(product);
                }
                } offerRepository.save(offer);
            return offer;
            }

//chatgpt given method with nullcheck
//
//    @Override
//    public Offer update(OfferDto offerDto) {
//        long id = offerDto.getId();
//        Offer offer = offerRepository.findById(id);
//        offer.setName(offerDto.getName());
//        offer.setDescription(offerDto.getDescription());
//        offer.setOffPercentage(offerDto.getOffPercentage());
//
//        // Check if offerDto.getOfferType() is not null before invoking equals
//        if (offerDto.getOfferType() != null && offerDto.getOfferType().equals("Product")) {
//            if (offer.getOfferProductId() != null) {
//                if (offerDto.getOfferProductId() != offer.getOfferProductId()) {
//                    updateProductPrice(offer.getOfferProductId());
//                }
//            } else {
//                updateProductPrice(offer.getOfferCategoryId());
//                offer.setOfferCategoryId(null);
//            }
//            Product product = productService.findById(offerDto.getOfferProductId());
//            offer.setOfferProductId(offerDto.getOfferProductId());
//            Double oldDiscount = (Double) product.getCostPrice() * ((double) offerDto.getOffPercentage() / 100.0);
//            String formattedDiscount = String.format("%.2f", oldDiscount);
//            Double discount = Double.parseDouble(formattedDiscount);
//            String formattedSalePrice = String.format("%.2f", product.getCostPrice() - discount);
//            Double salePrice = Double.parseDouble(formattedSalePrice);
//            product.setSalePrice(salePrice);
//            offer.setApplicableForProductName(product.getName());
//            productRepository.save(product);
//        } else {
//            if (offer.getOfferCategoryId() != null) {
//                if (offerDto.getOfferCategoryId() != offer.getOfferCategoryId()) {
//                    updateCategoryPrice(offer.getOfferCategoryId());
//                }
//            } else {
//                updateProductPrice(offer.getOfferCategoryId());
//                offer.setOfferProductId(null);
//            }
//            long applicable_id = offerDto.getOfferCategoryId();
//            Category category = categoryService.findById(applicable_id);
//            offer.setOfferCategoryId(offerDto.getOfferCategoryId());
//            offer.setApplicableForCategoryName(category.getName());
//            List<Product> productList = productService.findProductsByCategory(category.getId());
//            for (Product product : productList) {
//                Double oldDiscount = (Double) product.getCostPrice() * ((double) offerDto.getOffPercentage() / 100.0);
//                String formattedDiscount = String.format("%.2f", oldDiscount);
//                Double discount = Double.parseDouble(formattedDiscount);
//                String formattedSalePrice = String.format("%.2f", product.getCostPrice() - discount);
//                Double salePrice = Double.parseDouble(formattedSalePrice);
//                product.setSalePrice(salePrice);
//                productRepository.save(product);
//            }
//        }
//        offerRepository.save(offer);
//        return offer;
//    }


    @Override
    public void disable(long id) {
        Offer offer=offerRepository.findById(id);
        offer.setEnabled(false);
        if(offer.getOfferType().equals("Product")){
            Product product=productService.findById(offer.getOfferProductId());
            product.setSalePrice(0);
            productRepository.save(product);
        }else{
            long applicable_id=offer.getOfferCategoryId();
            Category category=categoryService.findById(applicable_id);
            List<Product>productList=productService.findProductsByCategory(category.getId());
            for(Product product:productList){
                product.setSalePrice(0);
                productRepository.save(product);
            }
        }
    }

    @Override
    public void enable(long id) {
        Offer offer=offerRepository.findById(id);
        offer.setEnabled(true);
        if (offer.getOfferType().equals("Product")){
            Product product=productService.findById(offer.getOfferProductId());
            Double oldDiscount= (Double)product.getCostPrice() * ((double)offer.getOffPercentage()/100.0);
            String formattedDiscount = String.format("%.2f",oldDiscount);
            Double discount= Double.parseDouble(formattedDiscount);
            String formattedSalePrice = String.format("%.2f", product.getCostPrice() - discount);
            Double salePrice= Double.parseDouble(formattedSalePrice);
            product.setSalePrice(salePrice);
            productRepository.save(product);
        }else{
            long applicable_id=offer.getOfferCategoryId();
            Category category= categoryService.findById(applicable_id);
            List<Product> productList = productService.findProductsByCategory(category.getId());
            for(Product product : productList){
                Double oldDiscount= (Double)product.getCostPrice() * ((double)offer.getOffPercentage()/100.0);
                String formattedDiscount = String.format("%.2f",oldDiscount);
                Double discount= Double.parseDouble(formattedDiscount);
                String formattedSalePrice = String.format("%.2f", product.getCostPrice() - discount);
                Double salePrice= Double.parseDouble(formattedSalePrice);
                product.setSalePrice(salePrice);
                productRepository.save(product);
            }
        }
    }

    @Override
    public void deleteOffer(long id) {
        Offer offer=offerRepository.findById(id);
        if(offer.getOfferType().equals("Product")){
            Product product=productService.findById(offer.getOfferProductId());
            if(product!=null){
                product.setSalePrice(0);
                productRepository.save(product);
            }
        }else{
            long applicable_id=offer.getOfferCategoryId();
            Category category=categoryService.findById(applicable_id);
            List<Product>productList=productService.findProductsByCategory(category.getId());
            for(Product product:productList){
                if(product!=null){
                    product.setSalePrice(0);
                    productRepository.save(product);
                }
            }
        }

        offerRepository.deleteById(offer.getId());
    }

    @Override
    public OfferDto findById(long id) {
        Offer offer=offerRepository.findById(id);
        OfferDto offerDto=new OfferDto();
        offerDto.setId(offer.getId());
        offerDto.setName(offer.getName());
        offerDto.setDescription(offer.getDescription());
        offerDto.setOffPercentage(offer.getOffPercentage());
        offerDto.setOfferType(offer.getOfferType());
        if(offer.getOfferType().equals("Product")){
            offerDto.setOfferProductId(offer.getOfferProductId());
            offerDto.setApplicableForProductName(offer.getApplicableForCategoryName());
        }
        else{
            offerDto.setOfferCategoryId(offer.getOfferCategoryId());
            offerDto.setApplicableForCategoryName(offer.getApplicableForCategoryName());
        }
        offerDto.setEnabled(offer.isEnabled());
        return offerDto;
    }


    public void updateProductPrice(Long id) {
     Product product = productService.findById(id);
     product.setSalePrice(0);
     productRepository.save(product);
 }
        public void updateCategoryPrice(long id){
         Category category = categoryService.findById(id);
         List<Product> productList = productService.findProductsByCategory(category.getId());
         for (Product product : productList) {
             product.setSalePrice(0);
             productRepository.save(product);
         }
     }


//    public List<OfferDto>transfer(List<Offer>offerList){
//        List<OfferDto>offerDtoList=new ArrayList<>();
//        for(Offer offer:offerList){
//            OfferDto offerDto=new OfferDto();
//            offerDto.setId(offer.getId());
//            offerDto.setName(offer.getName());
//            offerDto.setDescription(offer.getDescription());
//            offerDto.setOffPercentage(offer.getOffPercentage());
//            offerDto.setOfferType(offer.getOfferType());
//            System.out.println(offer.getOfferType());
//            if(offer.getOfferType().equals("Product")){
//                offerDto.setOfferProductId(offer.getOfferProductId());
//                offerDto.setApplicableForProductName(offer.getApplicableForProductName());
//            }else{
//                offerDto.setOfferCategoryId(offer.getOfferCategoryId());
//                offerDto.setApplicableForCategoryName(offer.getApplicableForCategoryName());
//            }
//            offerDto.setEnabled(offer.isEnabled());
//            offerDtoList.add(offerDto);
//        }
//        return offerDtoList;
//    }


    public List<OfferDto> transfer(List<Offer> offerList) {
        List<OfferDto> offerDtoList = new ArrayList<>();

        for (Offer offer : offerList) {
            OfferDto offerDto = new OfferDto();
            offerDto.setId(offer.getId());
            offerDto.setName(offer.getName());
            offerDto.setDescription(offer.getDescription());
            offerDto.setOffPercentage(offer.getOffPercentage());
            offerDto.setOfferType(offer.getOfferType());
            System.out.println(offer.getOfferType());

            // Check if getOfferType() is not null before invoking equals
            if (offer.getOfferType() != null && offer.getOfferType().equals("Product")) {
                offerDto.setOfferProductId(offer.getOfferProductId());
                offerDto.setApplicableForProductName(offer.getApplicableForProductName());
            } else if (offer.getOfferType() != null) {
                offerDto.setOfferCategoryId(offer.getOfferCategoryId());
                offerDto.setApplicableForCategoryName(offer.getApplicableForCategoryName());
            }

            offerDto.setEnabled(offer.isEnabled());
            offerDtoList.add(offerDto);
        }

        return offerDtoList;
    }





}
