package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import com.beenotice.demo.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
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
    private SanityCheckInventory sanityCheckInventory;

    @Test
    void scenarios_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/admin/scenarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void scenarios_withAdminRole_returnsOkAndModel() throws Exception {
        var checks = List.of(
                new SanityCheck("ctx", "q?", new Decision("A", "cA"), new Decision("B", "cB")));
        when(sanityCheckInventory.findAll()).thenReturn(checks);

        mockMvc.perform(get("/admin/scenarios").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/scenarios"))
                .andExpect(model().attribute("scenarios", checks));
    }
}
