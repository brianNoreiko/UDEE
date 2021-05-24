package com.utn.UDEE.service;

import com.utn.UDEE.models.Person;
import com.utn.UDEE.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service

public class PersonService {
    @Autowired
    PersonRepository personRepository;

    public void addPerson(Person person) { personRepository.save(person); }

    public Person getPersonById(Long id) {
        return personRepository.findById(id).orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    public List<Person> getAll() { return personRepository.findAll();}
}
