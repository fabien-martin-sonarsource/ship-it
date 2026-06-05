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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SanityCheckInventory inventory;

    @Test
    void scenarios_returns401WithoutCredentials() throws Exception {
        mockMvc.perform(get("/admin/scenarios")).andExpect(status().isUnauthorized());
    }

    @Test
    void scenarios_returns200WithAdminCredentials() throws Exception {
        when(inventory.findAll()).thenReturn(List.of(anyCheck()));

        mockMvc.perform(get("/admin/scenarios").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/scenarios"))
                .andExpect(model().attributeExists("scenarios"));
    }

    @Test
    void scenarios_exposesViewDtosNotDomainModels() throws Exception {
        when(inventory.findAll()).thenReturn(List.of(anyCheck()));

        mockMvc.perform(get("/admin/scenarios").with(user("admin").roles("ADMIN")))
                .andExpect(model().attribute("scenarios", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.instanceOf(SanityCheckView.class))));
    }

    private SanityCheck anyCheck() {
        return new SanityCheck(
                "context",
                "question?",
                new Decision("A", "consequence A"),
                new Decision("B", "consequence B"));
    }
}
