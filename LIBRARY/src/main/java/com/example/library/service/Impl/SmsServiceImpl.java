package com.example.library.service.Impl;

import com.example.library.customerConfig.TwilioConfig;
import com.example.library.service.SmsService;
import com.twilio.Twilio;
import org.springframework.stereotype.Service;
import com.twilio.type.PhoneNumber;
import com.twilio.rest.api.v2010.account.Message;

import java.text.DecimalFormat;
import java.util.Random;


@Service
public class SmsServiceImpl implements SmsService {

    private TwilioConfig twilioConfig;

    public SmsServiceImpl(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
    }
    @Override
    public String generateOtp() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

//    @Override
//    public void sendOtp(String otp) {
//        try {
//            PhoneNumber to = new PhoneNumber("+918921164620+");//to
//            PhoneNumber from = new PhoneNumber(twilioConfig.getPhoneNumber()); // from
//            String otpMessage = "Dear Customer , Your OTP is  " + otp + " for sending sms through Spring boot application. Thank You.";
//            Message message = Message
//                    .creator(to, from,
//                            otpMessage)
//                    .create();
//        } catch (Exception e) {
//            e.printStackTrace();// add manual exception
//        }
//    }




    @Override
    public void sendOtp(String otp) {
        try {
            String phoneNumber="+14787073420";
            Twilio.init(System.getenv("AccountSID"),System.getenv("AuthToken"));
            PhoneNumber to = new PhoneNumber("+918921164620");//to
            PhoneNumber from = new PhoneNumber(phoneNumber); // from
            String otpMessage = "Dear Customer , Your OTP is  " + otp + " for sending sms through Spring boot application. Thank You.";
            Message message = Message
                    .creator(to, from,
                            otpMessage)
                    .create();
        } catch (Exception e) {
            throw new OtpSendException("Error sending OTP: " + e.getMessage()) ;
  }

}















}

