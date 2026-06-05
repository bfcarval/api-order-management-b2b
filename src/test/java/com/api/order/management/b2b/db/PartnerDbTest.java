package com.api.order.management.b2b.db;

import com.api.order.management.b2b.db.repository.PartnerRepository;
import com.api.order.management.b2b.dto.PartnerDTO;
import com.api.order.management.b2b.exception.DatabaseException;
import com.api.order.management.b2b.exception.ResourceNotFoundException;
import com.api.order.management.b2b.model.PartnerModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartnerDbTest {

    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private PartnerDb partnerDb;

    @Test
    @DisplayName("1. Deve salvar um parceiro comercial com sucesso e retornar o modelo persistido")
    void shouldSavePartnerSuccessfully() {
        var partnerDto = PartnerDTO.builder()
                .name("Nova Distribuidora S.A.")
                .creditLimit(new BigDecimal("75000.00"))
                .build();

        var expectedPartner = PartnerModel.builder()
                .id(1L)
                .name("Nova Distribuidora S.A.")
                .creditLimit(new BigDecimal("75000.00"))
                .build();

        when(partnerRepository.save(any(PartnerModel.class))).thenReturn(expectedPartner);

        var result = partnerDb.save(partnerDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Nova Distribuidora S.A.", result.getName());
        assertEquals(new BigDecimal("75000.00"), result.getCreditLimit());
        verify(partnerRepository, times(1)).save(any(PartnerModel.class));
    }

    @Test
    @DisplayName("2. Deve estourar DatabaseException se o repositório falhar ao salvar")
    void shouldThrowDatabaseExceptionWhenSaveFails() {
        var partnerDto = PartnerDTO.builder()
                .name("Parceiro de Teste")
                .creditLimit(BigDecimal.ZERO)
                .build();

        when(partnerRepository.save(any(PartnerModel.class))).thenThrow(new RuntimeException("Database offline"));

        var exception = assertThrows(DatabaseException.class, () -> partnerDb.save(partnerDto));

        assertEquals("Falha interna ao cadastrar o parceiro comercial no banco de dados.", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("3. Deve atualizar os dados ou saldo de um parceiro com sucesso")
    void shouldUpdatePartnerSuccessfully() {
        var partnerModel = PartnerModel.builder()
                .id(10L)
                .name("Parceiro Atualizado")
                .creditLimit(new BigDecimal("5000.00"))
                .build();

        when(partnerRepository.save(partnerModel)).thenReturn(partnerModel);

        var result = partnerDb.update(partnerModel);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Parceiro Atualizado", result.getName());
        verify(partnerRepository, times(1)).save(partnerModel);
    }

    @Test
    @DisplayName("4. Deve estourar DatabaseException se o repositório falhar na atualização")
    void shouldThrowDatabaseExceptionWhenUpdateFails() {
        var partnerModel = PartnerModel.builder().id(10L).build();
        when(partnerRepository.save(partnerModel)).thenThrow(new RuntimeException("Lock conflict"));

        var exception = assertThrows(DatabaseException.class, () -> partnerDb.update(partnerModel));

        assertEquals("Falha interna ao atualizar os dados do parceiro no banco de dados.", exception.getMessage());
    }

    @Test
    @DisplayName("5. Deve retornar o parceiro ao buscar por um ID válido")
    void shouldReturnPartnerWhenFoundById() {
        var partnerId = 1L;
        var expectedPartner = PartnerModel.builder().id(partnerId).name("Parceiro Localizado").build();
        when(partnerRepository.findById(partnerId)).thenReturn(Optional.of(expectedPartner));

        var result = partnerDb.findById(partnerId);

        assertNotNull(result);
        assertEquals(partnerId, result.getId());
        assertEquals("Parceiro Localizado", result.getName());
    }

    @Test
    @DisplayName("6. Deve estourar ResourceNotFoundException se o parceiro por ID não existir")
    void shouldThrowResourceNotFoundExceptionWhenPartnerDoesNotExist() {
        var partnerId = 999L;
        when(partnerRepository.findById(partnerId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> partnerDb.findById(partnerId));

        assertEquals("Parceiro não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("7. Deve estourar DatabaseException caso ocorra um erro técnico imprevisto na busca por ID")
    void shouldThrowDatabaseExceptionWhenFindByIdFails() {
        var partnerId = 1L;
        when(partnerRepository.findById(partnerId)).thenThrow(new RuntimeException("SQL Error"));

        var exception = assertThrows(DatabaseException.class, () -> partnerDb.findById(partnerId));

        assertEquals("Falha ao processar consulta de parceiro.", exception.getMessage());
    }

    @Test
    @DisplayName("8. Deve retornar a lista completa de todos os parceiros cadastrados")
    void shouldReturnAllPartnersList() {
        var partners = List.of(
                PartnerModel.builder().id(1L).name("Parceiro A").build(),
                PartnerModel.builder().id(2L).name("Parceiro B").build()
        );

        when(partnerRepository.findAll()).thenReturn(partners);

        var result = partnerDb.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Parceiro A", result.get(0).getName());
        verify(partnerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("9. Deve estourar DatabaseException se a listagem geral falhar por infraestrutura")
    void shouldThrowDatabaseExceptionWhenFindAllFails() {
        when(partnerRepository.findAll()).thenThrow(new RuntimeException("Connection pool exhausted"));

        var exception = assertThrows(DatabaseException.class, () -> partnerDb.findAll());

        assertEquals("Falha de infraestrutura ao processar listagem geral de parceiros.", exception.getMessage());
    }
}
