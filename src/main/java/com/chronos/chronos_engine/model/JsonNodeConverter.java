package com.chronos.chronos_engine.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Requires Lombok
@Converter(autoApply = true)
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {
    // ObjectMapper is thread-safe, static is fine
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonNode node) {
        if (node == null) return null;
        try {
            return node.toString();
        } catch (Exception e) {
            log.error("JSON writing error", e);
            throw new RuntimeException("Failed to convert JSON to String", e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        try {
            return mapper.readTree(dbData);
        } catch (JsonProcessingException e) {
            log.error("JSON reading error", e);
            // Depending on business logic, might return null or throw
            return null;
        }
    }
}