package com.beenotice.demo.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void root_isAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void adminScenarios_returns401WithoutCredentials() throws Exception {
        mockMvc.perform(get("/admin/scenarios")).andExpect(status().isUnauthorized());
    }

    @Test
    void adminScenarios_returns200WithAdminCredentials() throws Exception {
        mockMvc.perform(get("/admin/scenarios").with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/scenarios"));
    }

    @Test
    void adminScenarios_listsAllLoadedScenarios() throws Exception {
        mockMvc.perform(get("/admin/scenarios").with(httpBasic("admin", "admin")))
                .andExpect(model().attribute("scenarios", hasSize(8)));
    }
}
