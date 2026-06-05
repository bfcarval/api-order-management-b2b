package com.api.order.management.b2b.mapper;

import com.api.order.management.b2b.controller.response.PartnerResponse;
import com.api.order.management.b2b.dto.PartnerDTO;
import com.api.order.management.b2b.model.PartnerModel;

public final class PartnerMapper {

    public static PartnerDTO fromModelToDTO(final PartnerModel partnerModel) {
        return PartnerDTO.builder()
                .id(partnerModel.getId())
                .name(partnerModel.getName())
                .creditLimit(partnerModel.getCreditLimit())
                .build();
    }

    public static PartnerResponse fromDTOToResponse(final PartnerDTO partnerDTO) {
        return new PartnerResponse(
                partnerDTO.getId(),
                partnerDTO.getName(),
                partnerDTO.getCreditLimit()
        );
    }
}
