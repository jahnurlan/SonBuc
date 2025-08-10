package com.example.planms.config;

import com.example.planms.model.enums.GoalType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GoalTypeConverter implements Converter<String, GoalType> {

    @Override
    public GoalType convert(String source) {
        try {
            return GoalType.valueOf(source.toUpperCase()); // Büyük harfe çevirerek Enum'a dönüştür
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid GoalType: " + source);
        }
    }
}
