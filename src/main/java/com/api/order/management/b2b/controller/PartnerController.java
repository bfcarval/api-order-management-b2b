package com.api.order.management.b2b.controller;

import com.api.order.management.b2b.controller.interfaces.PartnerControllerAPI;
import com.api.order.management.b2b.controller.request.PartnerRequest;
import com.api.order.management.b2b.controller.response.PartnerResponse;
import com.api.order.management.b2b.mapper.PartnerMapper;
import com.api.order.management.b2b.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.api.order.management.b2b.mapper.PartnerMapper.fromDTOToResponse;

@RequiredArgsConstructor
@RestController
public class PartnerController implements PartnerControllerAPI {

    private final PartnerService partnerService;

    @Override
    public ResponseEntity<PartnerResponse> create(PartnerRequest partnerRequest) {
        return new ResponseEntity<>(
                fromDTOToResponse(partnerService.createPartner(partnerRequest)), HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<PartnerResponse> getById(Long id) {
        return ResponseEntity.ok(
                fromDTOToResponse(partnerService.getById(id))
        );
    }

    @Override
    public ResponseEntity<List<PartnerResponse>> getAll() {
        return ResponseEntity.ok(
                partnerService.getAll().stream().map(PartnerMapper::fromDTOToResponse).collect(Collectors.toList())
        );
    }
}
