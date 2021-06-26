package com.utn.UDEE.controller.backofficeController;

import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.exception.SinceUntilException;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.dto.MeasurementDto;
import com.utn.UDEE.models.dto.MeterDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.MeasurementService;
import com.utn.UDEE.utils.EntityResponse;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.utn.UDEE.utils.Utils.checkSinceUntil;

@RestController
@RequestMapping("/backoffice/measurement")

public class MeasurementBackOfficeController {

    MeasurementService measurementService;
    ConversionService conversionService;

    @Autowired
    public MeasurementBackOfficeController(MeasurementService measurementService, ConversionService conversionService) {
        this.measurementService = measurementService;
        this.conversionService = conversionService;
    }

    @PreAuthorize(value ="hasAuthority('EMPLOYEE')")
    @PostMapping("/")
    public ResponseEntity<Response> addMeasurement(@RequestBody Measurement measurement) throws ResourceAlreadyExistException, ResourceDoesNotExistException {
        Measurement measurementAdded = measurementService.addMeasurement(measurement);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(EntityURLBuilder.buildURL("measurements",measurementAdded.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Response.builder().message("Measurement created successfully").build());
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<MeasurementDto> getMeasurementById(@PathVariable Integer id) throws ResourceDoesNotExistException {
        MeasurementDto measurementDto = conversionService.convert(measurementService.getMeasurementById(id), MeasurementDto.class);
        return ResponseEntity.ok(measurementDto);
    }

    @PreAuthorize(value ="hasAuthority('EMPLOYEE')")
    @GetMapping("/")
    public ResponseEntity<List<MeasurementDto>> getAllMeasurements(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                   @RequestParam(value = "size", defaultValue = "10") Integer size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Measurement> measurementPage = measurementService.getAllMeasurements(pageable);
        Page<MeasurementDto> measurementDtoPage = measurementPage.map(measurement -> conversionService.convert(measurementPage, MeasurementDto.class));
        return EntityResponse.listResponse(measurementDtoPage);
    }

    //Consulta de mediciones de un domicilio por rango de fechas
    @PreAuthorize(value ="hasAuthority('EMPLOYEE')")
    @GetMapping("/addresses/{idAddress}")
    public ResponseEntity<List<MeasurementDto>> getMeasurementByAddressBetweenDate(@PathVariable Integer idAddress,
                                                                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                                   @RequestParam(value = "since", defaultValue = "2021-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime since,
                                                                                   @RequestParam(value = "until", defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime until) throws SinceUntilException, ResourceDoesNotExistException {
        checkSinceUntil(since,until);
        Pageable pageable = PageRequest.of(page,size);
        Page<Measurement> measurementPage =  measurementService.getMeasurementByAddressBetweenDate(idAddress,since,until,pageable);
        Page<MeasurementDto> measurementDtos = measurementPage.map(measurement -> conversionService.convert(measurement, MeasurementDto.class));
        return EntityResponse.listResponse(measurementDtos);
    }


}
