package com.utn.UDEE.utils;

import com.utn.UDEE.models.dto.AddressDto;


import com.utn.UDEE.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;

import static com.utn.UDEE.utils.MeterUtilsTest.aMeter;

public class AddressUtilsTest {

    public static Address aAddress() {
        return Address.builder().id(1).street("calle falsa").number(123).meter(aMeter()).build();
    }

    public static AddressDto aAddressDto(){
        return new AddressDto();
    }

    public static Page<Address> aAddressPage() {
        return new PageImpl<>(List.of(aAddress()));
    }

    public static Page<Address> aAddressPageEmpty() {
        List<Address> addressList = Collections.emptyList();
        return new PageImpl<>(addressList);
    }

    }
