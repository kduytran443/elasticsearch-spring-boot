package com.kduytran.elasticsearch.controller;

import com.kduytran.elasticsearch.constant.ResponseConstant;
import com.kduytran.elasticsearch.document.VehicleDocument;
import com.kduytran.elasticsearch.dto.ResponseDTO;
import com.kduytran.elasticsearch.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDocument> getById(@PathVariable String id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

}
