package com.example.customer.Controller;

public class OutOfStockException  extends  RuntimeException{
    public OutOfStockException(String message)
    {
        super(message);
    }

}
