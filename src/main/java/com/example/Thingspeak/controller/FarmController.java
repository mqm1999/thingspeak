package com.example.Thingspeak.controller;

import com.example.Thingspeak.entity.DataRecord;
import com.example.Thingspeak.service.FarmService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@RestController
@RequestMapping("/farm")
@RequiredArgsConstructor
public class FarmController {
    private final FarmService farmService;

    @GetMapping
    public ResponseEntity<List<DataRecord>> getAllDataRecord() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(farmService.getAllDataRecord());
    }

    @PostMapping("/latest-data")
    public ResponseEntity<Boolean> updateLatestData() throws JsonProcessingException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(farmService.updateLatestData());
    }
}
