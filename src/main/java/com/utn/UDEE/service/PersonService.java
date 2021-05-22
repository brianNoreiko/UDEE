package com.utn.UDEE.service;

import com.utn.UDEE.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class PersonService {
    @Autowired
    private PersonRepository personRepository;


}
