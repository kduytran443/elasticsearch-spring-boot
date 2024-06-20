package com.kduytran.elasticsearch.service;

import com.kduytran.elasticsearch.document.PersonDocument;
import com.kduytran.elasticsearch.repo.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    public void save(final PersonDocument document) {
        personRepository.save(document);
    }

    public PersonDocument findById(final String id) {
        return personRepository.findById(id).orElse(null);
    }

}
