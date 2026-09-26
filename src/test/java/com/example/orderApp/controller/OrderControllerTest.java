package com.example.orderApp.controller;

import com.example.orderApp.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldReturn400WhenTotalHasMoreThanTwoDecimalPlaces() throws Exception {
        String payload = """
            {
                "customerId": "1",
                "total": 100.011
            }
        """;

        mockMvc.perform(
            post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200WhenTotalHasTwoDecimalPlaces() throws Exception {
        String payload = """
            {
                "customerId": "1",
                "total": 100.00
            }
        """;

        mockMvc.perform(
            post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        ).andExpect(status().isOk());
    }

    @Test
    void shouldReturn200WhenTotalHasNoDecimalPlaces() throws Exception {
        String payload = """
            {
                "customerId": "1",
                "total": 100
            }
        """;

        mockMvc.perform(
            post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        ).andExpect(status().isOk());
    }
}