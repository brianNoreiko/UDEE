package com.utn.UDEE.controller.androidAppController;

import com.utn.UDEE.service.InvoiceService;
import com.utn.UDEE.service.MeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/measurements")
public class MeasurementAppController {

    MeasurementService measurementService;
    ConversionService conversionService;
    InvoiceService invoiceService;

    @Autowired
    public MeasurementAppController(MeasurementService measurementService, ConversionService conversionService, InvoiceService invoiceService) {
        this.measurementService = measurementService;
        this.conversionService = conversionService;
        this.invoiceService = invoiceService;
    }

}



