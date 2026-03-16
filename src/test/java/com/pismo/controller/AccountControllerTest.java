package com.pismo.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAccountFailInvalidDocumentNumber() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "documentNumber": "123456789"
                        }
                        """))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("Document number must be exactly 11 digits"));
    }

    @Test
    void createAccountFailExistingDocumentNumber() throws Exception {
        String documentNumber = "00" + System.currentTimeMillis() % 1000000000L; // Generate a unique 11-digit document number
        MvcResult result = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "documentNumber": "%s"
                        }
                        """.formatted(documentNumber)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Account created successfully"))
                .andReturn();

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "documentNumber": "%s"
                        }
                        """.formatted(documentNumber)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("An account with this document number already exists"));
    }

    @Test
    void getAccountFailAccountNotFound() throws Exception {
        mockMvc.perform(get("/accounts/{accountId}", "1234567890")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("message").value("Account not found with id: 1234567890"));
    }

    @Test
    void getAccountSuccess() throws Exception {
        String documentNumber = "00" + System.currentTimeMillis() % 1000000000L; // Generate a unique 11-digit document number
        MvcResult result = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "documentNumber": "%s"
                        }
                        """.formatted(documentNumber)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Account created successfully"))
                .andReturn();

        String accountId = JsonPath.read(result.getResponse().getContentAsString(), "$.data.accountId");

        mockMvc.perform(get("/accounts/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("accountId").value(accountId));
    }
}
