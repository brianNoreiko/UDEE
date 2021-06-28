package com.utn.UDEE.utils;

import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.dto.AddressDto;
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
        AddressDto addressDto = new AddressDto();
        addressDto.setId(1);
        addressDto.setStreet("San Lorenzo");
        addressDto.setNumber(2371);
        addressDto.setApartment("1° A");
        return addressDto;
    }

    public static Page<Address> aAddressPage() {
        return new PageImpl<>(List.of(aAddress()));
    }

    public static Page<Address> aAddressEmptyPage() {
        List<Address> addressList = Collections.emptyList();
        return new PageImpl<>(addressList);
    }

    public static Page<AddressDto> aAddressDtoPage(){
        return new PageImpl<>(List.of(aAddressDto()));
    }

    }
