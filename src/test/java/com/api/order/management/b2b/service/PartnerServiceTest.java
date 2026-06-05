package com.api.order.management.b2b.service;

import com.api.order.management.b2b.controller.request.PartnerRequest;
import com.api.order.management.b2b.db.PartnerDb;
import com.api.order.management.b2b.dto.PartnerDTO;
import com.api.order.management.b2b.exception.BusinessException;
import com.api.order.management.b2b.exception.DatabaseException;
import com.api.order.management.b2b.exception.ResourceNotFoundException;
import com.api.order.management.b2b.mapper.PartnerMapper;
import com.api.order.management.b2b.model.PartnerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartnerServiceTest {

    @Mock
    private PartnerDb partnerDb;

    @InjectMocks
    private PartnerService partnerService;

    private PartnerRequest partnerRequest;
    private PartnerModel partnerModel;
    private PartnerDTO partnerDTO;

    @BeforeEach
    void setUp() {
        partnerRequest = new PartnerRequest("Parceiro B2B", new BigDecimal("5000.00"));
        partnerModel = mock(PartnerModel.class);
        partnerDTO = PartnerDTO.builder().id(1L).name("Parceiro B2B").creditLimit(new BigDecimal("5000.00")).build();
    }

    @Test
    void createPartnerSuccess() {
        when(partnerDb.save(any(PartnerDTO.class))).thenReturn(partnerModel);

        try (MockedStatic<PartnerMapper> mapper = mockStatic(PartnerMapper.class)) {
            mapper.when(() -> PartnerMapper.fromModelToDTO(partnerModel)).thenReturn(partnerDTO);

            PartnerDTO result = partnerService.createPartner(partnerRequest);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Parceiro B2B", result.getName());
            verify(partnerDb).save(any(PartnerDTO.class));
        }
    }

    @Test
    void createPartnerDatabaseException() {
        when(partnerDb.save(any(PartnerDTO.class))).thenThrow(new DatabaseException("Erro de banco"));

        assertThrows(DatabaseException.class, () -> partnerService.createPartner(partnerRequest));
    }

    @Test
    void createPartnerUnexpectedException() {
        when(partnerDb.save(any(PartnerDTO.class))).thenThrow(new RuntimeException("Erro genérico"));

        assertThrows(BusinessException.class, () -> partnerService.createPartner(partnerRequest));
    }

    @Test
    void getByIdSuccess() {
        when(partnerDb.findById(1L)).thenReturn(partnerModel);

        try (MockedStatic<PartnerMapper> mapper = mockStatic(PartnerMapper.class)) {
            mapper.when(() -> PartnerMapper.fromModelToDTO(partnerModel)).thenReturn(partnerDTO);

            PartnerDTO result = partnerService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(partnerDb).findById(1L);
        }
    }

    @Test
    void getByIdResourceNotFoundException() {
        when(partnerDb.findById(1L)).thenThrow(new ResourceNotFoundException("Não encontrado"));

        assertThrows(ResourceNotFoundException.class, () -> partnerService.getById(1L));
    }

    @Test
    void getByIdDatabaseException() {
        when(partnerDb.findById(1L)).thenThrow(new DatabaseException("Erro de banco"));

        assertThrows(DatabaseException.class, () -> partnerService.getById(1L));
    }

    @Test
    void getByIdUnexpectedException() {
        when(partnerDb.findById(1L)).thenThrow(new RuntimeException("Erro genérico"));

        assertThrows(BusinessException.class, () -> partnerService.getById(1L));
    }

    @Test
    void getAllSuccess() {
        when(partnerDb.findAll()).thenReturn(List.of(partnerModel));

        try (MockedStatic<PartnerMapper> mapper = mockStatic(PartnerMapper.class)) {
            mapper.when(() -> PartnerMapper.fromModelToDTO(partnerModel)).thenReturn(partnerDTO);

            List<PartnerDTO> result = partnerService.getAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).getId());
            verify(partnerDb).findAll();
        }
    }

    @Test
    void getAllEmptyList() {
        when(partnerDb.findAll()).thenReturn(Collections.emptyList());

        List<PartnerDTO> result = partnerService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(partnerDb).findAll();
    }

    @Test
    void getAllDatabaseException() {
        when(partnerDb.findAll()).thenThrow(new DatabaseException("Erro de banco"));

        assertThrows(DatabaseException.class, () -> partnerService.getAll());
    }

    @Test
    void getAllUnexpectedException() {
        when(partnerDb.findAll()).thenThrow(new RuntimeException("Erro genérico"));

        assertThrows(BusinessException.class, () -> partnerService.getAll());
    }
}
