package com.api.order.management.b2b.mapper;

import com.api.order.management.b2b.dto.PartnerDTO;
import com.api.order.management.b2b.model.PartnerModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PartnerMapperTest {

    @Test
    @DisplayName("1. Deve converter PartnerModel para PartnerDTO perfeitamente")
    void shouldConvertFromModelToDto() {
        var model = PartnerModel.builder()
                .id(1L)
                .name("Distribuidora de Bebidas Alfa")
                .creditLimit(new BigDecimal("50000.00"))
                .build();

        var dto = PartnerMapper.fromModelToDTO(model);

        assertNotNull(dto);
        assertEquals(model.getId(), dto.getId());
        assertEquals(model.getName(), dto.getName());
        assertEquals(model.getCreditLimit(), dto.getCreditLimit());
    }

    @Test
    @DisplayName("2. Deve converter PartnerDTO para PartnerResponse perfeitamente")
    void shouldConvertFromDtoToResponse() {
        var dto = PartnerDTO.builder()
                .id(2L)
                .name("Atacadista Central B2B")
                .creditLimit(new BigDecimal("125000.75"))
                .build();

        var response = PartnerMapper.fromDTOToResponse(dto);

        assertNotNull(response);
        assertEquals(dto.getId(), response.id());
        assertEquals(dto.getName(), response.name());
        assertEquals(dto.getCreditLimit(), response.creditLimit());
    }
}
