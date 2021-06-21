package com.utn.UDEE.controller.backofficeController;

import com.utn.UDEE.exception.SinceUntilException;
import com.utn.UDEE.models.Measurement;
import com.utn.UDEE.models.dto.MeasurementDto;
import com.utn.UDEE.service.MeasurementService;
import com.utn.UDEE.utils.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    //Consulta de mediciones de un domicilio por rango de fechas
    @PreAuthorize(value ="hasAuthority('EMPLOYEE')")
    @GetMapping("/addresses/{idAddress}")
    public ResponseEntity<List<MeasurementDto>> getMeasurementByAddressBetweenDate(@PathVariable Integer idAddress,
                                                                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                                   @RequestParam(value = "since", defaultValue = "2021-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate since,
                                                                                   @RequestParam(value = "until", defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate until) throws SinceUntilException {
        checkSinceUntil(since,until);
        Pageable pageable = PageRequest.of(page,size);
        Page<Measurement> measurementPage =  measurementService.getMeasurementByAddressBetweenDate(idAddress,since,until,pageable);
        Page<MeasurementDto> measurementDtos = measurementPage.map(measurement -> conversionService.convert(measurement, MeasurementDto.class));
        return EntityResponse.listResponse(measurementDtos);
    }
}
