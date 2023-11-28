package com.example.library.service;

import com.example.library.model.EmailDetails;

public interface EmailService {


    String sendSimpleEmail(EmailDetails emailDetails);
}
