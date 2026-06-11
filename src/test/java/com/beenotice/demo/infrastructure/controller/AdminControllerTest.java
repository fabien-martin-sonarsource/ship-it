package com.beenotice.demo.infrastructure.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void scenarios_returnsUnauthorized_whenNoCredentials() throws Exception {
        mockMvc.perform(get("/admin/scenarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void scenarios_returnsUnauthorized_whenWrongCredentials() throws Exception {
        mockMvc.perform(get("/admin/scenarios").with(httpBasic("admin", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void scenarios_returnsOk_withAdminCredentials() throws Exception {
        mockMvc.perform(get("/admin/scenarios").with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/scenarios"));
    }

    @Test
    void scenarios_rendersAllScenariosFromLoadedJson() throws Exception {
        mockMvc.perform(get("/admin/scenarios").with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Scenario Administration")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<table")));
    }

    @Test
    void publicRoot_remainsAccessible_withoutCredentials() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
