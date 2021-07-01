package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.PrimaryKeyViolationException;
import com.utn.UDEE.exception.ResourceAlreadyExistException;
import com.utn.UDEE.exception.ResourceDoesNotExistException;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.repository.MeterRepository;
import com.utn.UDEE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MeterService {

    MeterRepository meterRepository;
    UserRepository userRepository;

    @Autowired
    public MeterService(MeterRepository meterRepository, UserRepository userRepository) {
        this.meterRepository = meterRepository;
        this.userRepository = userRepository;
    }

    public Page<Meter> getAllMeters(Pageable pageable) {
        return meterRepository.findAll(pageable);
    }

    public Meter getMeterById(Integer id) throws ResourceDoesNotExistException {
        return meterRepository.findById(id).orElseThrow(() -> new ResourceDoesNotExistException("Meter doesn't exist"));
    }


    public Meter addMeter(Meter meter) throws ResourceAlreadyExistException {
        Boolean alreadyExist = meterRepository.existsById(meter.getSerialNumber());
        if (alreadyExist == false) {
            return meterRepository.save(meter);
        } else {
            throw new ResourceAlreadyExistException("Address already exists");
        }
    }

    public void deleteMeterById(Integer id) throws DeleteException, ResourceDoesNotExistException {
        Meter meter = getMeterById(id);
        if (meter == null){
            throw new ResourceDoesNotExistException("Meter doesn't exist");
        }
        if(meter.getAddress() == null) {
            meterRepository.deleteById(meter.getSerialNumber());
        } else {
            throw new DeleteException("It cannot be deleted because another object depends on it");
        }
    }

    public Meter updateMeter(Integer idToUp, Meter meter) throws ResourceAlreadyExistException, PrimaryKeyViolationException, ResourceDoesNotExistException {
        Meter meterToUpdate = getMeterById(idToUp);
        if(meterToUpdate.equals(meter)){
            throw new ResourceDoesNotExistException("Nothing to update. The meter already exist");
        }
        if(meter.getSerialNumber() != meterToUpdate.getSerialNumber()){
            throw new PrimaryKeyViolationException("Primary key (id) cannot be changed");
        }
        return meterRepository.save(meter);
    }
}
