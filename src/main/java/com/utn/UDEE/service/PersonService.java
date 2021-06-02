package com.utn.UDEE.service;

import com.utn.UDEE.models.Person;
import com.utn.UDEE.models.PostResponse;
import com.utn.UDEE.utils.EntityURLBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service

public class PersonService {
    private static final String PERSONA_PATH = "person";
    @Autowired
    PersonRepository personRepository;

    public PostResponse addPerson(Person person) {
        Person p = personRepository.save(person);
        return PostResponse
                .builder()
                .status(HttpStatus.CREATED)
                .url(EntityURLBuilder.buildURL(PERSONA_PATH, p.getId()))
                .build() ; }

    public Person getPersonById(Long id) {
        return personRepository.findById(id).orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    public List<Person> getAll() { return personRepository.findAll();}
}
