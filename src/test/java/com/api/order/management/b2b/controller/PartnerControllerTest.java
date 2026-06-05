package com.api.order.management.b2b.controller;

import com.api.order.management.b2b.controller.request.PartnerRequest;
import com.api.order.management.b2b.controller.response.PartnerResponse;
import com.api.order.management.b2b.dto.PartnerDTO;
import com.api.order.management.b2b.mapper.PartnerMapper;
import com.api.order.management.b2b.service.PartnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static com.api.order.management.b2b.mapper.PartnerMapper.fromDTOToResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PartnerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PartnerService partnerService;

    @InjectMocks
    private PartnerController partnerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(partnerController).build();
    }

    @Test
    @DisplayName("1. Deve retornar 201 Created ao cadastrar um novo parceiro com sucesso")
    void shouldReturn201WhenPartnerCreatedSuccessfully() throws Exception {
        var requestBody = "{\"name\": \"Nova Distribuidora S.A.\", \"creditLimit\": 75000.00}";
        var expectedResponse = new PartnerResponse(1L, "Nova Distribuidora S.A.", new BigDecimal("75000.00"));
        var mockDomainObject = mock(PartnerDTO.class);

        when(partnerService.createPartner(any(PartnerRequest.class))).thenReturn(mockDomainObject);

        try (MockedStatic<PartnerMapper> mapperMock = mockStatic(PartnerMapper.class)) {
            mapperMock.when(() -> PartnerMapper.fromDTOToResponse(any())).thenReturn(expectedResponse);

            mockMvc.perform(post("/api/partners")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Nova Distribuidora S.A."))
                    .andExpect(jsonPath("$.creditLimit").value(75000.00));
        }
    }

    @Test
    @DisplayName("2. Deve retornar 200 Ok ao buscar um parceiro por ID existente")
    void shouldReturn200WhenFindingPartnerById() throws Exception {
        var partnerId = 1L;
        var expectedResponse = new PartnerResponse(partnerId, "Nova Distribuidora S.A.", new BigDecimal("75000.00"));
        var mockDomainObject = mock(PartnerDTO.class);

        when(partnerService.getById(partnerId)).thenReturn(mockDomainObject);

        try (MockedStatic<PartnerMapper> mapperMock = mockStatic(PartnerMapper.class)) {
            mapperMock.when(() -> fromDTOToResponse(any())).thenReturn(expectedResponse);

            mockMvc.perform(get("/api/partners/" + partnerId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(partnerId))
                    .andExpect(jsonPath("$.name").value("Nova Distribuidora S.A."));
        }
    }

    @Test
    @DisplayName("3. Deve retornar 200 Ok e a lista de todos os parceiros comerciais cadastrados")
    void shouldReturn200AndAllPartnersList() throws Exception {
        var mockDomainObject1 = mock(PartnerDTO.class);
        var mockDomainObject2 = mock(PartnerDTO.class);
        var mockList = List.of(mockDomainObject1, mockDomainObject2);

        var partnerResponse1 = new PartnerResponse(1L, "Parceiro Alfa", new BigDecimal("50000.00"));
        var partnerResponse2 = new PartnerResponse(2L, "Parceiro Beta", new BigDecimal("30000.00"));

        when(partnerService.getAll()).thenReturn(mockList);

        try (MockedStatic<PartnerMapper> mapperMock = mockStatic(PartnerMapper.class)) {

            mapperMock.when(() -> fromDTOToResponse(mockDomainObject1)).thenReturn(partnerResponse1);
            mapperMock.when(() -> fromDTOToResponse(mockDomainObject2)).thenReturn(partnerResponse2);

            mockMvc.perform(get("/api/partners"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].name").value("Parceiro Alfa"))
                    .andExpect(jsonPath("$[1].id").value(2L))
                    .andExpect(jsonPath("$[1].name").value("Parceiro Beta"));
        }
    }
}
