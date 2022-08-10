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
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

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

    @Override
    public Boolean updateControlStatus(String device) throws JsonProcessingException, URISyntaxException {
        Boolean controlMode = Boolean.FALSE;
        if (updateLatestData()) {
            if (getAllDataRecord().isEmpty()) {
                return Boolean.FALSE;
            }
            // get control check for manual/auto (1: auto, 0: manual)
            controlMode = getAllDataRecord().get(getAllDataRecord().size() - 1).getControlCheck();
        }
        if (controlMode) {
            return Boolean.FALSE;
        }
        switch (device) {
            case Constant.LIGHT:
                String changeLampStatusUrl = Constant.LAMP_UPDATE_URL + convertStatusToString(!(getAllDataRecord().get(getAllDataRecord().size() - 1).getLampStatus()));
                URI lampUri = new URI(changeLampStatusUrl);
                restTemplate.exchange(lampUri, GET, null, Void.class);
                return Boolean.TRUE;
            case Constant.PUMP:
                String changePumpStatusUrl = Constant.PUMP_UPDATE_URL + convertStatusToString(!(getAllDataRecord().get(getAllDataRecord().size() - 1).getPumpStatus()));
                URI pumpUri = new URI(changePumpStatusUrl);
                restTemplate.exchange(pumpUri, GET, null, Void.class);
                return Boolean.TRUE;
            default:
                break;
        }
        return Boolean.TRUE;
    }

    private void createDataRecord(JsonNode node, LocalDateTime createdTime) {
        DataRecord singleRecord = DataRecord.builder()
                .airTemperature(node.get("field2").asDouble())
                .airHumidity(node.get("field3").asDouble())
                .lightIntensity(node.get("field1").asDouble())
                .soilMoisture(node.get("field4").asDouble())
                .lampStatus(convertIntegerToBoolean(node.get("field5").asInt()))
                .pumpStatus(convertIntegerToBoolean(node.get("field6").asInt()))
                .createdTime(createdTime)
                .updatedBy(UPDATED_USER)
                .controlCheck(convertIntegerToBoolean(node.get("field7").asInt()))
                .build();
        farmRepository.save(singleRecord);
    }

    private String convertStatusToString(Boolean status) {
        final String TRUE = "1";
        final String FALSE = "0";
        if (status.equals(Boolean.TRUE)) {
            return TRUE;
        }
        return FALSE;
    }

    private Boolean convertIntegerToBoolean(Integer status) {
        if (status == 1) {
            return Boolean.TRUE;
        } else if (status == 0) {
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }
}
