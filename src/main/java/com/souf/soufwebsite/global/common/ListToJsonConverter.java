package com.souf.soufwebsite.global.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class ListToJsonConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null) return "[]";
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("리스트 -> JSON 변환 실패", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return List.of();
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("JSON -> 리스트 변환 실패", e);
        }
    }
}