package com.kduytran.elasticsearch.controller;

import com.kduytran.elasticsearch.constant.ResponseConstant;
import com.kduytran.elasticsearch.document.PersonDocument;
import com.kduytran.elasticsearch.dto.ResponseDTO;
import com.kduytran.elasticsearch.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/person")
@AllArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<ResponseDTO> save(@RequestBody PersonDocument document) {
        personService.save(document);
        return ResponseEntity.ok(new ResponseDTO(ResponseConstant.STATUS_201, ResponseConstant.MESSAGE_201));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDocument> findById(@PathVariable final String id) {
        return ResponseEntity.ok(personService.findById(id));
    }

}
