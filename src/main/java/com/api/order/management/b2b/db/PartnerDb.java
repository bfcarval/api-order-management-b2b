package com.api.order.management.b2b.db;

import com.api.order.management.b2b.db.repository.PartnerRepository;
import com.api.order.management.b2b.dto.PartnerDTO;
import com.api.order.management.b2b.exception.DatabaseException;
import com.api.order.management.b2b.exception.ResourceNotFoundException;
import com.api.order.management.b2b.model.PartnerModel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PartnerDb {

    private static final Logger log = LoggerFactory.getLogger(PartnerDb.class);

    private final PartnerRepository partnerRepository;

    @Transactional
    public PartnerModel save(final PartnerDTO partnerDTO) {
        try {
            log.info("Iniciando cadastro de parceiro comercial B2B: {}", partnerDTO.getName());

            final var partner = PartnerModel.builder()
                    .name(partnerDTO.getName())
                    .creditLimit(partnerDTO.getCreditLimit())
                    .build();

            final var savedPartner = partnerRepository.save(partner);
            log.info("Parceiro comercial cadastrado com sucesso. ID gerado: {}", savedPartner.getId());
            return savedPartner;
        } catch (Exception e) {
            log.error("Falha técnica ao salvar o parceiro comercial ({}). Erro: {}", partnerDTO.getName(), e.getMessage(), e);
            throw new DatabaseException("Falha interna ao cadastrar o parceiro comercial no banco de dados.", e);
        }
    }

    @Transactional
    public PartnerModel update(final PartnerModel partnerModel) {
        try {
            log.info("Iniciando atualização cadastral/saldo do parceiro ID: {}", partnerModel.getId());

            final var updatedPartner = partnerRepository.save(partnerModel);
            log.info("Dados do parceiro ID: {} atualizados com sucesso.", updatedPartner.getId());
            return updatedPartner;
        } catch (Exception e) {
            log.error("Falha técnica ao atualizar dados do parceiro ID: {}. Erro: {}", partnerModel.getId(), e.getMessage(), e);
            throw new DatabaseException("Falha interna ao atualizar os dados do parceiro no banco de dados.", e);
        }
    }

    @Transactional
    public PartnerModel findById(final Long partnerId) {
        try {
            log.info("Buscando parceiro por ID: {}", partnerId);

            return partnerRepository.findById(partnerId)
                    .orElseThrow(() -> {
                        log.warn("Parceiro ID: {} não foi localizado no sistema.", partnerId);
                        return new ResourceNotFoundException("Parceiro não encontrado");
                    });
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro técnico inesperado ao buscar parceiro ID: {}. Erro: {}", partnerId, e.getMessage(), e);
            throw new DatabaseException("Falha ao processar consulta de parceiro.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<PartnerModel> findAll() {
        try {
            log.info("Buscando lista completa de parceiros comerciais.");

            return partnerRepository.findAll();
        } catch (Exception e) {
            log.error("Erro técnico ao listar todos os parceiros comerciais do banco. Erro: {}", e.getMessage(), e);
            throw new DatabaseException("Falha de infraestrutura ao processar listagem geral de parceiros.", e);
        }
    }
}
