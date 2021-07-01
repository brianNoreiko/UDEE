package com.utn.UDEE.controller.backofficeController;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Address;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.models.dto.MeterDto;
import com.utn.UDEE.models.responses.Response;
import com.utn.UDEE.service.MeterService;
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
@RequestMapping("/backoffice/meter")
public class MeterBackOfficeController {

    MeterService meterService;
    ConversionService conversionService;

    @Autowired
    public MeterBackOfficeController(MeterService meterService, ConversionService conversionService) {
        this.meterService = meterService;
        this.conversionService = conversionService;
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PostMapping("/")
    public ResponseEntity<Response> addMeter(@RequestBody Meter meter) throws ResourceAlreadyExistException {
        Meter meterAdded = meterService.addMeter(meter);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(EntityURLBuilder.buildURL("meters",meterAdded.getSerialNumber()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(EntityResponse.messageResponse("Meter created successfully"));
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @GetMapping("/id")
    public ResponseEntity<MeterDto> getMeterById(@RequestParam(value = "id") Integer id) throws HttpClientErrorException, ResourceDoesNotExistException {
        Meter meter = meterService.getMeterById(id);
        MeterDto meterDto = conversionService.convert(meter, MeterDto.class);
        return ResponseEntity.ok(meterDto);
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @GetMapping("/")
    public ResponseEntity<List<MeterDto>> getAllMeters(@RequestParam(value = "size", defaultValue = "0") Integer page,
                                                       @RequestParam(value = "page", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Meter> meterPage = meterService.getAllMeters(pageable);
        Page<MeterDto> meterDtoPage = meterPage.map(meter -> conversionService.convert(meter,MeterDto.class));
        return EntityResponse.listResponse(meterDtoPage);
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMeterById(@PathVariable Integer id) throws DeleteException, ResourceDoesNotExistException {
        meterService.deleteMeterById(id);
        return ResponseEntity.accepted().build();
    }

    @PreAuthorize(value = "hasAuthority('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateMeter(@PathVariable Integer id,
                                                  @RequestBody Meter meter) throws PrimaryKeyViolationException, ResourceAlreadyExistException, ResourceDoesNotExistException {
        Meter meterUpdated = meterService.updateMeter(id,meter);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(EntityResponse.messageResponse("Meter updated successfully"));
    }

}
