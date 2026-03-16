package com.pismo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "documentNumber": "12345678900"
                        }
                        """))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("message").value("Failed to create account"));
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
        mockMvc.perform(get("/accounts/{accountId}", "98ad8f95-0572-449a-82ce-a5a5a577b844")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("accountId").value("98ad8f95-0572-449a-82ce-a5a5a577b844"));
    }
}
