package com.api.order.management.b2b.service;

import com.api.order.management.b2b.controller.request.PartnerRequest;
import com.api.order.management.b2b.db.PartnerDb;
import com.api.order.management.b2b.dto.PartnerDTO;
import com.api.order.management.b2b.exception.BusinessException;
import com.api.order.management.b2b.exception.DatabaseException;
import com.api.order.management.b2b.exception.ResourceNotFoundException;
import com.api.order.management.b2b.mapper.PartnerMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.api.order.management.b2b.mapper.PartnerMapper.fromModelToDTO;

@RequiredArgsConstructor
@Service
public class PartnerService {

    private static final Logger log = LoggerFactory.getLogger(PartnerService.class);

    private final PartnerDb partnerDb;

    public PartnerDTO createPartner(final PartnerRequest partnerRequest) {
        log.info("Processando solicitação de criação de parceiro. Nome: {}", partnerRequest.name());

        try {
            final var partnerDtoToSave = PartnerDTO.builder()
                    .name(partnerRequest.name())
                    .creditLimit(partnerRequest.creditLimit())
                    .build();

            final var savedPartnerDto = fromModelToDTO(partnerDb.save(partnerDtoToSave));

            log.info("Parceiro criado com sucesso na camada de negócio. ID: {}", savedPartnerDto.getId());

            return savedPartnerDto;
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao processar criação de parceiro ({}). Erro: {}", partnerRequest.name(), e.getMessage(), e);
            throw new BusinessException("Falha ao processar as regras de negócio para criação do parceiro.", e);
        }
    }

    public PartnerDTO getById(final Long id) {
        log.info("Processando busca de parceiro por ID: {}", id);

        try {
            final var partnerDto = fromModelToDTO(partnerDb.findById(id));

            log.info("Parceiro ID: {} localizado e mapeado com sucesso.", id);

            return partnerDto;
        } catch (ResourceNotFoundException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao processar busca de parceiro ID: {}. Erro: {}", id, e.getMessage(), e);
            throw new BusinessException("Falha ao processar a consulta do parceiro.", e);
        }
    }

    public List<PartnerDTO> getAll() {
        log.info("Processando listagem geral de parceiros.");

        try {
            final var partners = partnerDb.findAll().stream()
                    .map(PartnerMapper::fromModelToDTO)
                    .collect(Collectors.toList());

            log.info("Listagem finalizada. Total de parceiros encontrados: {}", partners.size());

            return partners;
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao processar listagem de parceiros. Erro: {}", e.getMessage(), e);
            throw new BusinessException("Falha ao processar a listagem geral de parceiros.", e);
        }
    }
}
