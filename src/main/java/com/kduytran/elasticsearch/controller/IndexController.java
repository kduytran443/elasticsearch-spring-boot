package com.kduytran.elasticsearch.controller;

import com.kduytran.elasticsearch.constant.ResponseConstant;
import com.kduytran.elasticsearch.document.VehicleDocument;
import com.kduytran.elasticsearch.dto.ResponseDTO;
import com.kduytran.elasticsearch.service.IndexService;
import com.kduytran.elasticsearch.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/index")
@RestController
@RequiredArgsConstructor
public class IndexController {
    private final IndexService indexService;

    @PostMapping("/recreate")
    public void recreateAllIndices() {
        indexService.recreateIndices(true);
    }

}
