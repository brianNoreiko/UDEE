package com.utn.UDEE.converter;

import com.utn.UDEE.models.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static com.utn.UDEE.utils.AddressUtilsTest.aAddress;
import static com.utn.UDEE.utils.AddressUtilsTest.aAddressDto;
import static com.utn.UDEE.utils.BrandUtilsTest.aBrand;
import static com.utn.UDEE.utils.BrandUtilsTest.aBrandDto;
import static com.utn.UDEE.utils.InvoiceUtilsTest.aInvoice;
import static com.utn.UDEE.utils.InvoiceUtilsTest.aInvoiceDto;
import static com.utn.UDEE.utils.MeasurementUtilsTest.aMeasurement;
import static com.utn.UDEE.utils.MeasurementUtilsTest.aMeasurementDto;
import static com.utn.UDEE.utils.MeterUtilsTest.aMeter;
import static com.utn.UDEE.utils.MeterUtilsTest.aMeterDto;
import static com.utn.UDEE.utils.ModelUtilsTest.aModel;
import static com.utn.UDEE.utils.ModelUtilsTest.aModelDto;
import static com.utn.UDEE.utils.RateUtilsTest.aRate;
import static com.utn.UDEE.utils.RateUtilsTest.aRateDto;
import static com.utn.UDEE.utils.UserUtilsTest.aUser;
import static com.utn.UDEE.utils.UserUtilsTest.aUserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Converters {

    private ModelMapper modelMapper;


    private ConvertToAddressDto convertToAddressDto;
    private ConvertToBrandDto convertToBrandDto;
    private ConvertToInvoiceDto convertToInvoiceDto;
    private ConvertToMeasurementDto convertToMeasurementDto;
    private ConvertToMeterDto convertToMeterDto;
    private ConvertToModelDto convertToModelDto;
    private ConvertToRateDto convertToRateDto;
    private ConvertToUserDto convertToUserDto;


    @BeforeEach
    public void setUp() {
        modelMapper = mock(ModelMapper.class);

        convertToAddressDto = new ConvertToAddressDto(modelMapper);
        convertToBrandDto = new ConvertToBrandDto(modelMapper);
        convertToInvoiceDto = new ConvertToInvoiceDto(modelMapper);
        convertToMeasurementDto = new ConvertToMeasurementDto(modelMapper);
        convertToMeterDto = new ConvertToMeterDto(modelMapper);
        convertToModelDto = new ConvertToModelDto(modelMapper);
        convertToRateDto = new ConvertToRateDto(modelMapper);
        convertToUserDto = new ConvertToUserDto(modelMapper);
    }

    @Test
    public void convertAddress() {
        when(modelMapper.map(aAddress(), AddressDto.class)).thenReturn(aAddressDto());
        AddressDto addressDto = convertToAddressDto.convert(aAddress());
        assertEquals(aAddressDto(), addressDto);
    }

    @Test
    public void convertBrand() {
        when(modelMapper.map(aBrand(), BrandDto.class)).thenReturn(aBrandDto());
        BrandDto brandDto = convertToBrandDto.convert(aBrand());
        assertEquals(aBrandDto(), brandDto);
    }

    @Test
    public void convertInvoice() {
        when(modelMapper.map(aInvoice(), InvoiceDto.class)).thenReturn(aInvoiceDto());
        InvoiceDto invoiceDto = convertToInvoiceDto.convert(aInvoice());
        assertEquals(aInvoiceDto(), invoiceDto);
    }

    @Test
    public void convertMeasurement() {
        when(modelMapper.map(aMeasurement(), MeasurementDto.class)).thenReturn(aMeasurementDto());
        MeasurementDto measurementDto = convertToMeasurementDto.convert(aMeasurement());
        assertEquals(aMeasurementDto(),measurementDto);
    }

    @Test
    public void convertMeter() {
        when(modelMapper.map(aMeter(), MeterDto.class)).thenReturn(aMeterDto());
        MeterDto meterDto = convertToMeterDto.convert(aMeter());
        assertEquals(aMeterDto(),meterDto);
    }

    @Test
    public void convertModel() {
        when(modelMapper.map(aModel(), ModelDto.class)).thenReturn(aModelDto());
        ModelDto modelDto = convertToModelDto.convert(aModel());
        assertEquals(aModelDto(),modelDto);
    }

    @Test
    public void convertRate() {
        when(modelMapper.map(aRate(), RateDto.class)).thenReturn(aRateDto());
        RateDto rateDto = convertToRateDto.convert(aRate());
        assertEquals(aRateDto(),rateDto);
    }

    @Test
    public void convertUser() {
        when(modelMapper.map(aUser(),UserDto.class)).thenReturn(aUserDto());
        UserDto userDto = convertToUserDto.convert(aUser());
        assertEquals(aUserDto(),userDto);
    }

}