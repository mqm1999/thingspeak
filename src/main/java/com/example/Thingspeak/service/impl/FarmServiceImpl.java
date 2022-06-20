package com.example.Thingspeak.service.impl;

import com.example.Thingspeak.constant.Constant;
import com.example.Thingspeak.entity.DataRecord;
import com.example.Thingspeak.helper.TimeConverter;
import com.example.Thingspeak.repository.FarmRepository;
import com.example.Thingspeak.service.FarmService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FarmServiceImpl implements FarmService {
    private final FarmRepository farmRepository;
    private final RestTemplate restTemplate;
    private final TimeConverter timeConverter;
    private static final String UPDATED_USER = "MinhMQ1";

    @Override
    public List<DataRecord> getAllDataRecord() {
        return farmRepository.findAll();
    }

    @Override
    public Boolean updateLatestData() throws JsonProcessingException {
        String result = restTemplate.getForObject(Constant.URL, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode jsonNode = objectMapper.readTree(result);

        ArrayNode feeds = (ArrayNode) jsonNode.get("feeds");
        ArrayList<JsonNode> dataNodeList = new ArrayList<>();
        for (JsonNode node : feeds) {
            dataNodeList.add(node);
        }
        for (JsonNode node : dataNodeList) {
            Instant createdTimeInstant = timeConverter.convertZuluToInstant(node.get("created_at").asText());
            LocalDateTime createdTime = timeConverter.convertInstantToLDT(createdTimeInstant);
            DataRecord queryData = farmRepository.findFirstByOrderByCreatedTimeDesc();
            if (queryData == null) {
                createDataRecord(node, createdTime);
            } else {
                if (queryData.getCreatedTime().isBefore(createdTime)) {
                    createDataRecord(node, createdTime);
                }
            }
        }
        return Boolean.TRUE;
    }

    private void createDataRecord(JsonNode node, LocalDateTime createdTime) {
        DataRecord singleRecord = DataRecord.builder()
                .airTemperature(node.get("field2").asDouble())
                .airHumidity(node.get("field3").asDouble())
                .lightIntensity(node.get("field1").asDouble())
                .soilMoisture(node.get("field4").asDouble())
                .lampStatus(node.get("field5").asBoolean())
                .pumpStatus(node.get("field6").asBoolean())
                .createdTime(createdTime)
                .updatedBy(UPDATED_USER)
                .build();
        farmRepository.save(singleRecord);
    }
}
