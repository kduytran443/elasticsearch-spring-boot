package com.kduytran.elasticsearch.controller;

import com.kduytran.elasticsearch.constant.ResponseConstant;
import com.kduytran.elasticsearch.document.VehicleDocument;
import com.kduytran.elasticsearch.dto.ResponseDTO;
import com.kduytran.elasticsearch.search.SearchRequestDTO;
import com.kduytran.elasticsearch.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequestMapping("/api/vehicle")
@RestController
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<ResponseDTO> save(@RequestBody VehicleDocument document) {
        if (vehicleService.index(document)) {
            return ResponseEntity.ok(new ResponseDTO(ResponseConstant.STATUS_201, ResponseConstant.MESSAGE_201));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/list")
    public ResponseEntity<ResponseDTO> save(@RequestBody VehicleDocument[] documents) {
        if (vehicleService.index(documents)) {
            return ResponseEntity.ok(new ResponseDTO(ResponseConstant.STATUS_201, ResponseConstant.MESSAGE_201));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDocument> getById(@PathVariable String id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    @PostMapping("/search")
    public ResponseEntity<List<VehicleDocument>> search(@RequestBody SearchRequestDTO dto) {
        return ResponseEntity.ok(vehicleService.search(dto));
    }

    @GetMapping("/search/{date}")
    public ResponseEntity<List<VehicleDocument>> search(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") final Date date) {
        return ResponseEntity.ok(vehicleService.getAllCreatedSince(date));
    }

    @PostMapping("/search-date/{date}")
    public ResponseEntity<List<VehicleDocument>> search(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") final Date date,
                                                        @RequestBody SearchRequestDTO dto) {
        return ResponseEntity.ok(vehicleService.searchAllCreatedSince(dto, date));
    }

}
