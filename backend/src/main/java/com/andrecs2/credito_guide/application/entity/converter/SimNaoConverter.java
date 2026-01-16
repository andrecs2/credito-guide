package com.andrecs2.credito_guide.application.entity.converter;

import com.andrecs2.credito_guide.application.entity.enums.SimNao;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SimNaoConverter
        implements AttributeConverter<SimNao, Boolean> {

    public Boolean convertToDatabaseColumn(SimNao v) {
        return v == SimNao.SIM;
    }

    public SimNao convertToEntityAttribute(Boolean v) {
        return v ? SimNao.SIM : SimNao.NAO;
    }
}