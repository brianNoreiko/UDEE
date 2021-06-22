package com.utn.UDEE.service;

import com.utn.UDEE.exception.DeleteException;
import com.utn.UDEE.exception.alreadyExist.MeterAlreadyExist;
import com.utn.UDEE.exception.doesNotExist.MeterDoesNotExist;
import com.utn.UDEE.models.Meter;
import com.utn.UDEE.repository.MeterRepository;
import com.utn.UDEE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import static java.util.Objects.isNull;

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

    public Meter getMeterById(Integer id) {
        return meterRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }


    public Meter addMeter(Meter meter) throws MeterAlreadyExist {
        Meter alreadyExist = getMeterById(meter.getSerialNumber());
        if (isNull(alreadyExist)) {
            return meterRepository.save(meter);
        } else {
            throw new MeterAlreadyExist("Address already exists");
        }
    }

    public void deleteMeterById(Integer id) throws DeleteException, MeterDoesNotExist {
        Meter meter = getMeterById(id);
        if (meter == null){
            throw new MeterDoesNotExist("Meter doesn't exist");
        }
        if(isNull(meter.getAddress())) {
            meterRepository.deleteById(id);
        } else {
            throw new DeleteException("It cannot be deleted because another object depends on it");
        }
    }
}
