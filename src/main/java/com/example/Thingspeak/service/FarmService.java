package com.example.Thingspeak.service;

import com.example.Thingspeak.entity.DataRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FarmService {
    List<DataRecord> getAllDataRecord();

    Boolean updateLatestData() throws JsonProcessingException;
}
