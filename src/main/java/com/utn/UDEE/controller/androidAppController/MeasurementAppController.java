package com.utn.UDEE.controller.androidAppController;

import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.exception.SinceUntilException;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.dto.MeasurementDto;
import com.utn.UDEE.models.responses.ClientConsuption;
import com.utn.UDEE.service.InvoiceService;
import com.utn.UDEE.service.MeasurementService;
import com.utn.UDEE.utils.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.utn.UDEE.utils.Utils.checkSinceUntil;

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

    @GetMapping("/")
    public ResponseEntity<List<MeasurementDto>> getAllMeasurementsByUser(@PathVariable Integer idUser,
                                                                         @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                         @RequestParam(value = "size", defaultValue = "10") Integer size) throws ResourceDoesNotExistException {
        Pageable pageable = PageRequest.of(page,size);
        Page<Measurement> measurementPage  = measurementService.getAllMeasurementsByUser(idUser, pageable);
        Page<MeasurementDto> measurementDtoPage = measurementPage.map(measurement -> conversionService.convert(measurement, MeasurementDto.class));
        return EntityResponse.listResponse(measurementDtoPage);
    }

    //Consulta de consumo por rango de fechas (el usuario va a ingresar un rango de fechas y quiere saber cuánto consumió en ese periodo en Kwh y dinero)
    @GetMapping("/meters/{idMeter}/consumptions")
    public ResponseEntity<Optional<ClientConsuption>> getConsumptionsBetweenDate(@PathVariable Integer idMeter,
                                                                                 @RequestParam(value = "since", defaultValue = "2021-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime since,
                                                                                 @RequestParam(value = "until", defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime until)
            throws SinceUntilException, ResourceDoesNotExistException {
        checkSinceUntil(since, until);
        System.out.println(since);
        System.out.println(until);
        Optional<ClientConsuption> clientConsuption = measurementService.getConsumptionByMeterAndBetweenDate(idMeter, since, until);
        return ResponseEntity.status(HttpStatus.OK).body(clientConsuption);

    }

    //Consulta de mediciones por rango de fechas
    @GetMapping("/meters/{id}")
    public ResponseEntity<List<MeasurementDto>> getMeasurementsBetweenDate(@PathVariable Integer idMeter,
                                                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                           @RequestParam(value = "since", defaultValue = "2021-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime since,
                                                                           @RequestParam(value = "until", defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime until) throws SinceUntilException, ResourceDoesNotExistException {
        checkSinceUntil(since,until);
        Pageable pageable = PageRequest.of(page, size);
        Page<Measurement> measurementPage = measurementService.getAllByMeterAndBetweenDate(idMeter,since,until, pageable);
        Page<MeasurementDto> measurementDtos = measurementPage.map(measurement -> conversionService.convert(measurement, MeasurementDto.class));
        return EntityResponse.listResponse(measurementDtos);
    }

}



