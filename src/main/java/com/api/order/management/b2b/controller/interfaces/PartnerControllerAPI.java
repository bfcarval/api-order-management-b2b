package com.api.order.management.b2b.controller.interfaces;

import com.api.order.management.b2b.controller.request.PartnerRequest;
import com.api.order.management.b2b.controller.response.PartnerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/partners")
@Tag(name = "B2B Partners API", description = "Endpoints de gerenciamento e consulta de parceiros e limites de crédito")
public interface PartnerControllerAPI {

    @PostMapping
    @Operation(summary = "Cadastrar um novo parceiro comercial B2B")
    ResponseEntity<PartnerResponse> create(@RequestBody @Valid PartnerRequest partnerRequest);

    @GetMapping("/{id}")
    @Operation(summary = "Consultar dados e limite de crédito atual de um parceiro por ID")
    ResponseEntity<PartnerResponse> getById(@PathVariable Long id);

    @GetMapping
    @Operation(summary = "Listar todos os parceiros cadastrados")
    ResponseEntity<List<PartnerResponse>> getAll();
}
