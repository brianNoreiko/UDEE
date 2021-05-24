package com.utn.UDEE.controller;

import com.utn.UDEE.models.Person;
import com.utn.UDEE.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")

public class PersonController {

    @Autowired
    PersonService personService;

    @PostMapping
    public void addPerson(@RequestBody Person person){
        personService.addPerson(person);
    }

    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable Long id){ return personService.getPersonById(id);}

    @GetMapping
    public List<Person> getPersons(){ return personService.getAll();}



}
