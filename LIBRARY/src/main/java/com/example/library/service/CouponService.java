package com.example.library.service;

import com.example.library.dto.CouponDto;
import com.example.library.model.Coupon;

import java.util.List;

public interface CouponService {

    boolean findByCouponCode(String couponCode);
    Coupon  findByCode(String couponCode);
    double applyCoupon(String couponCode,double totalPrice);
    List<CouponDto> getAllCoupons();
    Coupon save(CouponDto couponDto);
    CouponDto findById(long id);

void disable(long id);
void enable(long id);
void deleteCoupon(long id);
    Coupon update(CouponDto couponDto);
}
