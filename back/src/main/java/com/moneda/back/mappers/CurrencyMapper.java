package com.moneda.back.mappers;

import com.moneda.back.dto.CurrencyDto;
import com.moneda.back.entities.Currency;
import org.springframework.stereotype.Component;

@Component
public class CurrencyMapper {
    public CurrencyDto toCurrencyDto(Currency currency) {
        CurrencyDto dto = new CurrencyDto();
        dto.setName(currency.getName());
        dto.setCode(currency.getCode());
        return dto;
    }
}

