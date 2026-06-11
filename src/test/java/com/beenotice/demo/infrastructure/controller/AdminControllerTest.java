package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import com.beenotice.demo.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SanityCheckInventory inventory;

    @Test
    void adminScenarios_returnsUnauthorized_whenNoCredentials() throws Exception {
        mockMvc.perform(get("/admin/scenarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminScenarios_returnsUnauthorized_whenInvalidCredentials() throws Exception {
        mockMvc.perform(get("/admin/scenarios").with(httpBasic("admin", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminScenarios_returnsForbidden_whenAuthenticatedWithoutAdminRole() throws Exception {
        mockMvc.perform(get("/admin/scenarios").with(user("regular").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminScenarios_returnsScenarioList_whenAuthenticatedAsAdmin() throws Exception {
        when(inventory.findAll()).thenReturn(List.of(
                new SanityCheck("ctx-1", "q-1?", new Decision("A1", "ca1"), new Decision("B1", "cb1")),
                new SanityCheck("ctx-2", "q-2?", new Decision("A2", "ca2"), new Decision("B2", "cb2"))
        ));

        mockMvc.perform(get("/admin/scenarios").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/scenarios"))
                .andExpect(model().attribute("scenarios", hasSize(2)));
    }
}
