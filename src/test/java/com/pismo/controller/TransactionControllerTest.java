package com.pismo.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createTransactionFailAccountRequired() throws Exception {
        String requestBody = """
                {
                    "operationType": 1,
                    "amount": 100.00
                }
                """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Account ID is required"));
    }

    @Test
    public void createTransactionFailInvalidAccount() throws Exception {
        String requestBody = """
                {
                    "accountId": "98786534",
                    "operationType": 1,
                    "amount": 100.00
                }
                """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("Account not found with id: 98786534"));
    }

    @Test
    void createTransactionFailOperationTypeRequired() throws Exception {
        String requestBody = """
                {
                    "accountId": "98ad8f95-0572-449a-82ce-a5a5a577b844",
                    "amount": 100.00
                }
                """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Operation type ID is required"));
    }

    @Test
    void createTransactionFailInvalidOperationType() throws Exception {
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

        String requestBody = """
                {
                    "accountId": "%s",
                    "operationType": 5,
                    "amount": 100.00
                }
                """.formatted(accountId);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("Invalid operation type: 5"));
    }

    @Test
    void createTransactionFailAmountZero() throws Exception {
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

        String requestBody = """
                {
                    "accountId": "%s",
                    "operationType": 1,
                    "amount": 0.00
                }
                """.formatted(accountId);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Amount is required and must not be zero"));
    }

    @Test
    void createTransactionSuccess() throws Exception {
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

        String requestBody = """
                {
                    "accountId": "%s",
                    "operationType": 1,
                    "amount": 100.00
                }
                """.formatted(accountId);


        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Transaction created successfully"))
                .andExpect(jsonPath("data.transactionId").exists());
    }
}
