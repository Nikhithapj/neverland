package com.example.library.service;

public interface SmsService {

    String generateOtp();
    void sendOtp(String otp);
}
