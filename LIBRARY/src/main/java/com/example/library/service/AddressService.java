package com.example.library.service;

import com.example.library.dto.AddressDto;
import com.example.library.model.Address;

public interface AddressService
{


    Address save(AddressDto addressDto,String username);
    AddressDto findById(long id);
    Address update(AddressDto addressDto,long id);

    Address findDefaultAddress(long customer_id);
    Address findByIdOrder(long id);
}
