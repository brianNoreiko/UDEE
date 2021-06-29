package com.utn.UDEE.controller.backofficeController;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Rate;
import com.utn.UDEE.models.dto.RateDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.RateService;
import com.utn.UDEE.utils.EntityResponse;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/backoffice/rate")
public class RateBackOfficeController {

    RateService rateService;
    ConversionService conversionService;

    @Autowired
    public RateBackOfficeController(RateService rateService, ConversionService conversionService) {
        this.rateService = rateService;
        this.conversionService = conversionService;
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @GetMapping("/")
    public ResponseEntity<List<RateDto>> getAllRates(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", defaultValue = "10") Integer size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Rate> ratePage = rateService.getAllRates(pageable);
        Page<RateDto> rateDtoPage = ratePage.map(rate -> conversionService.convert(ratePage, RateDto.class));
        return EntityResponse.listResponse(rateDtoPage);
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<RateDto> getRateById(@PathVariable Integer idRate) throws HttpClientErrorException, ResourceDoesNotExistException {
        Rate rate = rateService.getRateById(idRate);
        RateDto rateDto = conversionService.convert(rate, RateDto.class);
        return ResponseEntity.ok(rateDto);
    }


    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PostMapping("/")
    public ResponseEntity<Response> addRate(@RequestBody Rate rate) throws ResourceAlreadyExistException {
        Rate rateAdded = rateService.addRate(rate);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(EntityURLBuilder.buildURL("rates",rateAdded.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Response.builder().message("Rate created successfully").build());
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateRate(@PathVariable Integer idToUp,
                                               @RequestBody Rate newRate) throws ResourceDoesNotExistException, ResourceAlreadyExistException, PrimaryKeyViolationException {
        Rate rateSaved = rateService.updateRate(idToUp, newRate);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(EntityResponse.messageResponse("Rate updated successfully"));
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteRateById(@PathVariable Integer idRate) throws ResourceDoesNotExistException, DeleteException {
        rateService.deleteRateById(idRate);
        return ResponseEntity.accepted().build();
    }

}
