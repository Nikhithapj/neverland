
package com.example.library.service.Impl;

public class OutOfStockException  extends  RuntimeException{
    public OutOfStockException(String message)
    {
        super(message);
    }

}
